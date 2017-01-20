#!/usr/bin/python
# -*- coding: UTF-8 -*-

import textwrap

import tornado.httpserver
import tornado.ioloop
import tornado.options
import tornado.web

import smtplib
from email.mime.text import MIMEText
from email.header import Header
from email.mime.multipart import MIMEMultipart

import imaplib, string, email

import json

import time

#设置接收端口为7001
from tornado.options import define, options
define("port", default=7001, help="run on the given port", type=int)

emailList = {}

def checkMail():
    global emailList

    conn = imaplib.IMAP4_SSL("127.0.0.1")
    conn.login("***","******")
    conn.select("INBOX")

    type, data = conn.search(None, 'UNSEEN')
    print data
    if data[0] == '':
        print ""
    else:
        print data
        mail = {}
        msgList = data[0].split()
        last = msgList[len(msgList) - 1]
        type, data=conn.fetch(last,'(RFC822)')
        msg = email.message_from_string(data[0][1])
        content = msg.get_payload(decode=True)
        mail["From"] = msg["From"].split('<')[1].split('>')[0]
        mail["Subject"] = msg["Subject"]
        mail["Date"] = msg["Date"]
        mail['content'] = content

        if emailList.has_key(msg["To"]):
            emailList[msg["To"]].append(mail)
        else:
            emailList[msg["To"]] = []
            emailList[msg["To"]].append(mail)
            print msg["To"]
    conn.close();
    conn.logout()

    time.sleep(3)

#发送告警邮件
def Send_Mail(receiver, sender, topic, content):
    mail_host="127.0.0.1"  #设置服务器
    mail_user="***"    #用户名
    mail_pass="******"   #口令

    receivers = receiver
    #接收邮件，可设置为你的QQ邮箱或者其他邮箱
    mail_content = content
    #邮件内容
    message = MIMEText(content, 'plain', 'utf-8')
    #发件人和收件人名称
    message['From'] = Header(sender, 'utf-8')
    message['To'] =  Header(receiver, 'utf-8')
    #邮件标题
    subject = topic
    message['Subject'] = Header(subject, 'utf-8')

    try:
        smtpObj = smtplib.SMTP()
        smtpObj.connect(mail_host, 25)    # 25 为 SMTP 端口号
        smtpObj.login(mail_user,mail_pass)
        smtpObj.sendmail('szy@sunzhongyang.com', 'szy@sunzhongyang.com', message.as_string())
        print "邮件发送成功"
    except smtplib.SMTPException:
        print "Error: 无法发送邮件"

#处理发件请求的类
class sendHandler(tornado.web.RequestHandler):
    def post(self):

        #获取报文
        result = ""
        text = self.request.body
        text = str(text)

        mail = json.loads(text)

        Send_Mail(mail['receiver'], mail['sender'], mail['topic'], mail['content'])

        #写入成功报文
        result = "success"
        self.write(result)

class checkHandler(tornado.web.RequestHandler):
    def post(self):
        global emailList

        #获取报文
        result = ""
        text = self.request.body
        text = str(text)

        if emailList.has_key(text):
            if len(emailList[text]) != 0:
                self.write(json.dumps(emailList[text].pop(0)))
            else:
                self.write('NO')
        else:
            self.write('NO')


if __name__ == "__main__":
    tornado.options.parse_command_line()
    app = tornado.web.Application(
        handlers=[
            (r"/send", sendHandler),
            (r"/check",checkHandler)
        ]
    )
    http_server = tornado.httpserver.HTTPServer(app)
    http_server.listen(options.port)
    tornado.ioloop.PeriodicCallback(checkMail, 3000).start()
    tornado.ioloop.IOLoop.instance().start()

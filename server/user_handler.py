#!/usr/bin/python
# -*- coding: UTF-8 -*-

import textwrap

import tornado.httpserver
import tornado.ioloop
import tornado.options
import tornado.web

import json

#设置接收端口为7000
from tornado.options import define, options
define("port", default=7000, help="run on the given port", type=int)

#设置用户名为键，用户密码为值的字典
user_password = {}

#处理登陆请求的类
class loginHandler(tornado.web.RequestHandler):
    def post(self):

        #获取报文
        result = ""
        text = self.request.body
        text = str(text)

        #从报文中取出用户名和密码
        userInformation = json.loads(text)
        print userInformation

        #根据正确与否决定返回报文
        if user_password.has_key(userInformation['username']):
            if user_password[userInformation['username']] == userInformation['password']:
                result = "OK"
            else:
                result = "Error password"
        else:
            result = "Error user"
        self.write(result)

#处理注册请求的类
class registerHandler(tornado.web.RequestHandler):
    def post(self):
        result = ""
        text = self.request.body
        text = str(text)

        userInformation = json.loads(text)
        print userInformation

        if user_password.has_key(userInformation['username']):
            result = "User exist"
        else:
            user_password[userInformation['username']] = userInformation['password']
            result = "OK"
        self.write(result)

class checkHandler(tornado.web.RequestHandler):
    def post(self):
        result = ""
        text = self.request.body
        text = str(text)

        userInformation = json.loads(text)
        print userInformation

        if user_password.has_key(userInformation['username']):
            result = "User exist"
        else:
            result = "OK"
        self.write(result)

#处理变更密码请求的类
class changeHandler(tornado.web.RequestHandler):
    def post(self):

        #获取报文
        result = ""
        text = self.request.body
        text = str(text)

        #从报文中获取用户名和新旧密码
        userInformation = json.loads(text)

        #根据密码的正确与否决定是否更改原密码，并返回指定内容
        if user_password.has_key(userInformation['username']):
            if user_password[userInformation['username']] == userInformation['oldpassword']:
                user_password[userInformation['username']] = userInformation['newpassword']
                result = "OK"
            else:
                result = "oldpassword wrong"
        else:
            result = "No account"
        self.write(result)

if __name__ == "__main__":
    tornado.options.parse_command_line()
    app = tornado.web.Application(
        handlers=[
            (r"/login", loginHandler),
            (r"/register",registerHandler),
        (r"/change", changeHandler),
        (r"/check", checkHandler)
        ]
    )
    http_server = tornado.httpserver.HTTPServer(app)
    http_server.listen(options.port)
    tornado.ioloop.IOLoop.instance().start()

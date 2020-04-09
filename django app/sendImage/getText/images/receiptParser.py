#!/usr/bin/python
# -*- coding: utf8 -*-
import re
import json

import requests

GOODNAME = "good_name"
FORITEM = "for_item"
QUANTITY = "quantity"
TOTAL = "total"
INN = "inn"
SHOPNAME = "shop_name"
ADDRESS = "address"
LINES = "lines"

def getINNValue(string):
    expression = ur'(?u)[^0-9]+'""
    numbers = re.compile(expression)
    return numbers.sub("", string)

def getJson(rawString):

    inn = 0
    shopName = ""
    address = ""
    addressPaths = 0
    receiptList = []

    expression = u"ООО|ЗАО|ОАО|ИП|000|ЗА0|0А0"
    findName = re.compile(expression)

    expression = u"ИНН"
    findINN = re.compile(expression)

    expression = u"г\.|д\.|ул\.|ш\.|обл\.|корп\.|улица|дом"
    findAddress = re.compile(expression)

    expression = ur'(?u)[^A-Za-zа-яА-ЯЁё ]+'
    goodNameCut = re.compile(expression)

    expression = ur"(?u)[^0-9.' ]+"
    priceCut = re.compile(expression)

    expression = ur'[Э3З]+КЛ[З3]+|РЕГ|КАССА|ПТК|ДОК|ЧЕК|ПРОДАЖ|ПР0ДАЖ|ККМ|КАССА|ФП|ПРД|ТРН|СМЕНА|ЗВД'
    serviceWords = re.compile(expression)

    expression = ur'\. '
    dotSpacesCut = re.compile(expression)

    expression = ur"' "
    quoteSpacesCut = re.compile(expression)

    dictWhole = {}
    dictLines = []
    stringList = rawString.split("\n")
    for string in stringList:
        if shopName == "":
            if findName.search(string) is not None:
                shopName = string
                continue
        if addressPaths < 2:
            if findAddress.search(string) is not None:
                address += string + " "
                addressPaths += 1
                continue
        words = string.split()
        if inn == 0:
            n = len(words) - 1
            for i, word in enumerate(words):
                    if findINN.search(word.upper()) is not None:
                        if len(word) < 13:
                            if i < n:
                                inn = getINNValue(words[i+1])
                        else:
                            inn = getINNValue(word)
                            break
        if len(priceCut.sub("",string).split()) > 1:
            alphabeticSymbols = goodNameCut.sub("", string)
            if serviceWords.search(alphabeticSymbols.upper()) is None:
                alphabeticSymbols = ""
                digitSymols = ""
                started = False
                finished = False
                dictLine = {}
                for word in string.split():
                    if not finished:
                        wordPath = goodNameCut.sub("", word)
                        if len(wordPath) > 1:
                            started = True
                            alphabeticSymbols += word + " "
                        elif started:
                            finished = True
                    if finished:
                        digitSymols += priceCut.sub("", word) + " "
                prices = quoteSpacesCut.sub("", dotSpacesCut.sub(".", digitSymols)).split()
                if len(prices) == 3:
                    dictLine[GOODNAME] = alphabeticSymbols
                    dictLine[FORITEM] = prices[0]
                    dictLine[QUANTITY] = prices[1]
                    dictLine[TOTAL] = prices[2]
                if dictLine:
                    dictLines.append(dictLine)
    dictWhole[LINES] = dictLines
    inn = str(inn)
    dictWhole[INN] = inn
    if len(inn) == 12:
        inn = inn[2:]
    if len(inn) == 10:
        r = requests.get(u'https://огрн.онлайн/интеграция/компании/?инн='+ inn)
        if r.status_code == 200:
            jsonAnswer = r.json()
            if len(jsonAnswer) == 1:
                shopName = jsonAnswer[0][u"shortName"]
    dictWhole[ADDRESS] = address
    dictWhole[SHOPNAME] = shopName
    return json.dumps(dictWhole, ensure_ascii=False).encode('utf8')

def findAddress(string):
    expression = u"г\.|д\.|ул\.|ш\.|обл\.|корп\."
    search = re.compile(expression)
    # return search.findall(string)
    if search.search(string) is not None:
        return True
    else:
        return False


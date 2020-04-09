#!/usr/bin/python
# -*- coding: utf8 -*-
from PIL import Image, ImageDraw, ImageFont
import os
fontsFolder = "fonts"
shellResult = os.popen("ls " + fontsFolder + " | grep '.ttf'")
ls = shellResult.read()
ls = ls.split("\n")
fontsPath = [x for x in ls]
fontsPath = fontsPath[:-1]
start = ord(u'А')
end = ord(u'я') + 1
fontSize = 192
for font in fontsPath:
    # make a blank image for the text, initialized to transparent text color
    txt = Image.new('RGBA', (fontSize,fontSize), (255,255,255,0))
    path = "trainData/" + font[:-4]
    if not os.path.exists(path):
        os.makedirs(path)
    for i in xrange(start, end):

        symbol = unichr(i)
        # get a font
        fnt = ImageFont.truetype(fontsFolder + "/" + font, fontSize)
        # get a drawing context
        d = ImageDraw.Draw(txt)
        textSize =  d.textsize(symbol, font=fnt)

        txt = Image.new('RGBA', textSize, (255,255,255,0))
        d = ImageDraw.Draw(txt)
        # draw text, half opacity
        # d.text((10,10), u"Привет", font=fnt, fill=(255,255,255,128))
        # draw text, full opacity
        d.text((0,0), symbol, font=fnt, fill=(0,0,0,255))
        txt.save(path  + "/" + str(i) +".jpg")
from tesserocr import image_to_text
from tesserocr import PyTessBaseAPI, PSM
import cv2
# from pytesseract import image_to_string
import numpy as np
from PIL import ImageFile, Image
import receiptParser


def getText(rotation, image):
    ImageFile.LOAD_TRUNCATED_IMAGES = True
    image = Image.open(image).convert("RGB")
    b, g, r = image.split()
    image = Image.merge("RGB", (r, g, b))
    image = cv2.cvtColor(np.array(image), cv2.COLOR_BGR2GRAY)
    if rotation != 0:
        image = np.rot90(image, (360 - rotation) / 90)
    api = PyTessBaseAPI()
    api.SetImage(Image.fromarray(image))
    image = api.GetThresholdedImage()
    text = image_to_text(image, lang="rus")
    # print text
    return receiptParser.getJson(text)

def getTextTest():
    image = cv2.imread("okey-check6.jpg", 0)
    api = PyTessBaseAPI()
    api.SetImage(Image.fromarray(image))
    # print api.GetUTF8Text()
    image = api.GetThresholdedImage()
    image.save("rus.new.exp6.tiff")
    text = image_to_text(image, lang="rus")
    # text = image_to_string(image, lang="rus")
    print text
    return receiptParser.getJson(text)
getTextTest()

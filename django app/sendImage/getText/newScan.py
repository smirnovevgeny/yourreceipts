from tesserocr import image_to_text
from tesserocr import PyTessBaseAPI
import cv2
import numpy as np
from PIL import ImageFile, Image
import receiptParser


def getText(rotation, image):
    ImageFile.LOAD_TRUNCATED_IMAGES = True
    image = Image.open(image)
    image.save("loaded.jpg", dpi=(600, 600))
    image = cv2.imread("loaded.jpg", 0)
    if rotation != 0:
        image = np.rot90(image, (360 - rotation) / 90)
    # cv2.imwrite("rotated.jpg", image)
    api = PyTessBaseAPI()
    api.SetImage(Image.fromarray(image))
    image = api.GetThresholdedImage()
    text = image_to_text(image, lang="rus")
    return receiptParser.getJson(text)

def getTextTest():
    image = cv2.imread("images/eliten2.jpg", 0)
    api = PyTessBaseAPI()
    api.SetImage(Image.fromarray(image))
    # print api.GetUTF8Text()
    image = api.GetThresholdedImage()
    # image.save("rus.new.exp6.tiff")
    text = image_to_text(image, lang="rus")
    # text = image_to_string(image, lang="rus")
    print text
    return receiptParser.getJson(text)
# getTextTest()

from tesserocr import file_to_text
from tesserocr import PyTessBaseAPI

api = PyTessBaseAPI()
api.__init__(lang="rus")
api.SetImageFile("images/okey-micro.jpg")
api.GetThresholdedImage().save("dfd.png")
print file_to_text("dfd.png", lang="rus")
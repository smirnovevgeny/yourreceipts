# USAGE
# python scan.py --image images/page.jpg 

# import the necessary packages
# from pyimagesearch.transform import four_point_transform
# from pyimagesearch import imutils
from tesserocr import file_to_text, PSM, image_to_text
from PIL import Image
import numpy as np
import cv2
from skimage.filters import threshold_adaptive



def getText(rotation, image):
	# image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
	# nparr = np.fromstring(image, np.uint8)
	# image = cv2.imdecode(nparr, cv2.CV_LOAD_IMAGE_COLOR)
	image = cv2.cvtColor(np.array(image), cv2.COLOR_BGR2GRAY)
	# image = cv2.imread("name.jpeg", 0)
	# ratio = image.shape[0] / 500.0
	# orig = image.copy()
	# image = imutils.resize(image, height = 500)

	# convert the image to grayscale, blur it, and find edges
	# in the image
	# gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
	# gray = cv2.GaussianBlur(gray, (5, 5), 0)
	# edged = cv2.Canny(gray, 100, 200)

	# show the original image and the edge detected image
	print "STEP 1: Edge Detection"
	# cv2.imshow("Image", image)
	# cv2.imshow("Edged", edged)
	# cv2.waitKey(0)
	# cv2.destroyAllWindows()

	# find the contours in the edged image, keeping only the
	# largest ones, and initialize the screen contour
	# cnts = cv2.findContours(edged.copy(), cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)[0]
	# cnts = sorted(cnts, key = cv2.contourArea, reverse = False)
	# screenCnt = None
	# xMax = 0
	# yMax = 0
    #
	# height, width = gray.shape
	# xMin, yMin = width, height
	# # loop over the contours
	# for c in cnts:
	# 	# approximate the contour
	# 	peri = cv2.arcLength(c, True)
	# 	# print peri, yMin*ratio
	# 	approx = cv2.approxPolyDP(c, 0.02* peri, True)
	# 	for point in zip(*c)[0]:
	# 		x, y  = point
	# 		if x > xMax:
	# 			xMax = x
	# 		elif x < xMin:
	# 			xMin = x
	# 		if y > yMax:
	# 			yMax = y
	# 		elif y < yMin:
	# 			yMin = y
    #
	# # show the contour (outline) of the piece of paper
	# print "STEP 2: Find contours of paper"
	# # cv2.rectangle(image, (xMin, yMin), (xMax, yMax), (0, 255, 0), 2)
	# # cv2.imshow("Outline", image)
	# # cv2.waitKey(0)
	# # cv2.destroyAllWindows()
    #
	# # apply the four point transform to obtain a top-down
	# # view of the original image
	# # yMax = int(height - yMax)
	# # yMin = int(height - yMin)
	# area = (xMax - xMin) * (yMax - yMin)
	# areaWhole = width * height
	# areaPart = areaWhole / area
	# print areaPart
	# # print xMax, xMin, yMax, yMin
	# # todo crop image with more intelegence
	# if areaPart < 3:
	# 	warped = four_point_transform(orig, np.array([[[xMin, yMin]], [[xMin, yMax]], [[xMax, yMin]], [[xMax, yMax]]]).reshape(4, 2) * ratio)
	# else:
	# 	warped = orig
	# warped = gray[xMin:xMax, yMax:yMin]

	# convert the warped image to grayscale, then threshold it
	# to give it that 'black and white' paper effect
	# image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
	if rotation != 0:
		image = np.rot90(image, (360 - rotation) / 90)
	image = cv2.morphologyEx(image, cv2.MORPH_OPEN, cv2.getStructuringElement(cv2.MORPH_CROSS, (4,2)))
	kernel = np.ones((1,3),np.uint8)
	image = cv2.erode(image, kernel, iterations= 1)
	# kernel = np.ones((2,1),np.uint8)
	# warped = cv2.dilate(warped, kernel, iterations= 2)
	image = threshold_adaptive(image, 55, offset = 10, mode="nearest")
	# warped = cv2.adaptiveThreshold(warped, 200, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,\
	#             cv2.THRESH_BINARY, 29, 13)
	# cv2.imshow("Scanned", imutils.resize(warped, height = 650))
	# cv2.waitKey(0)
	# cv2.destroyAllWindows()
	image = image.astype("uint8") * 255
	cv2.imwrite("scanold.jpg", image)
	# cv2.imshow("1", warped)
	# cv2.waitKey(0)
	# cv2.imshow("2", warped)
	# cv2.waitKey(0)


	# show the original and scanned images
	print "STEP 3: Apply perspective transform"
	# cv2.imshow("Original", imutils.resize(orig, height = 650))
	# cv2.imshow("Scanned", imutils.resize(warped, height = 650))
	# cv2.imwrite("rus.new.exp6.tiff", image)
	# # cv2.waitKey(0)
	# text =  image_to_string(Image.open("warped.jpg"), lang="rus")
	text = file_to_text("scanold.jpg", lang="rus")
	# print text
	# symbols = image_to_string(Image.open("warped.jpg"), lang="rus", boxes=True).split()
	# symbolsN = len(symbols) / 6
	# height, width = warped.shape
	# # print symbols, warped.shape
	# for i in xrange(symbolsN):
	# 	start = (int(symbols[6 * i + 1]), height - int(symbols[ 6 * i + 2]))
	# 	end = (int(symbols[6 * i + 3]), height - int(symbols[ 6 * i + 4]))
	# 	# print start, end, symbols[6*i]
	# 	cv2.rectangle(warped, start, end, (0,255,0), 3)
	# cv2.imwrite("symbols.jpg", warped)
	print text
	return text
getText(0, image = cv2.imread("images/okey-check2.jpg"))
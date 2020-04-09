from django.http import HttpResponse
import getText.newScan as scan
from django.views.decorators.csrf import csrf_exempt


@csrf_exempt
def getImage(request):
    if request.method == 'POST':
        # form = UploadFileForm(request.POST, request.FILES)
        print "POST request"
        image = request.FILES["uploaded_file"]
	print "image here"
        rotation = int(request.META["HTTP_ORIENTATION"])
        # image = image.rotate(rotation)
        # image.save("name.jpeg")
        # regex = re.compile('^HTTP_')
        # args = dict((regex.sub('', header), value) for (header, value)
        #             in request.META.items() if header.startswith('HTTP_'))
        # print args
        # f = open("name.jpeg", "wb+")
        # for chunk in byteArray.chunks():
        #     f.write(chunk)
        json = scan.getText(rotation, image)


        return HttpResponse(json, content_type="application/json")
    else:
        return HttpResponse("Request has taken")




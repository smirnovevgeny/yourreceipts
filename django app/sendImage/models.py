from __future__ import unicode_literals
from django.db import models

class User(models.Model):
    name = models.CharField(max_length=30)
    email = models.EmailField()
    photo = models.ImageField()
    password = models.CharField(max_length=255)
    register_time = models.DateTimeField(auto_now_add=True)
    last_seen = models.DateTimeField()

class Shop(models.Model):
    name = models.CharField(max_length=255)
    inn = models.CharField(max_length=12)
    address = models.CharField(max_length=255)

class Good(models.Model):
    name = models.CharField(max_length=30)
    cost = models.IntegerField()
    shop = models.ForeignKey(Shop)

class Receipt(models.Model):
    goods = models.CharField(max_length=255)
    timestamp = models.DateTimeField()
    total = models.IntegerField()
    user = models.ForeignKey(User)
    good = models.ForeignKey(Good)
    shop = models.ForeignKey(Shop)




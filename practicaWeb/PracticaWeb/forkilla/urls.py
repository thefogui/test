from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^restaurants/$', views.restaurants, name='restaurants'),
    url(r'^restaurants/(?P<city>.*)/$', views.restaurants, name='restaurants'),
    url(r'^restaurant/(?P<restaurant_number>.*)/$', views.details, name='details'),
    url(r'^restaurants/(?P<city>.*)/(?P<category>.*)$', views.restaurants, name='restaurants'),
    url(r'^reservation/$', views.reservation, name='reservation'),
]
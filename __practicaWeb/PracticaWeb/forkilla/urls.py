from django.conf.urls import url
from django.conf.urls import include
from . import views


urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^restaurants/$', views.restaurants, name='restaurants'),
    url(r'^restaurants/(?P<city>.*)/$', views.restaurants, name='restaurants'),
    url(r'^restaurant/(?P<restaurant_number>.*)/$', views.details, name='details'),
    url(r'^restaurants/(?P<city>.*)/(?P<category>.*)$', views.restaurants, name='restaurants'),
    url(r'^reservation/$', views.reservation, name='reservation'),
    url(r'^checkout/$', views.checkout, name='checkout'),
    url(r'^restaurant/(?P<restaurant_number>.*)/comment$', views.add_comment, name='add_comment'),
    url(r'^restaurant/(?P<restaurant_number>.*)/review$', views.review, name='review'),
    url(r'^search/$', views.search_restaurant, name='search'),
    url(r'^register/$', views.register, name='register'),
    url(r'^delete/(?P<id>.*)$', views.deleteReservation, name='deleteReservation'),
    url(r'^reservationlist/$', views.reservationlist, name='reservationlist'),
]
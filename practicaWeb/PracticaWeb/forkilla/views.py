from django.shortcuts import render
from django.http import HttpResponse
from .models import Restaurant

# Create your views here.
def index(request):
    return render(request, 'forkilla/index.html')
    
def restaurants(request, city=""):
    promoted = False
    if city:
        restaurants_by_city = Restaurant.objects.filter(city__iexact=city)
    else:
        restaurants_by_city =  Restaurant.objects.filter(is_promot="True")
        promoted = True
    context = {
        'city': city,
        'restaurants': restaurants_by_city,
        'promoted': promoted
    }
    return render(request, 'forkilla/restaurants.html', context)
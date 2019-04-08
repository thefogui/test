from django.shortcuts import render
from django.http import HttpResponse
from .models import Restaurant

# Create your views here.
def index(request):
    return render(request, 'forkilla/index.html')
    
def restaurants(request, city="", category=""):
    promoted = False
    if city and category:
        restaurants_by_city = Restaurant.objects.filter(city__iexact=city, category__iexact=category)
    elif city:
        restaurants_by_city = Restaurant.objects.filter(city__iexact=city)
    else:
        restaurants_by_city =  Restaurant.objects.filter(is_promot="True")
        promoted = True
    context = {
        'city': city,
        'restaurants': restaurants_by_city.order_by('category'),
        'promoted': promoted
    }
    return render(request, 'forkilla/restaurants.html', context)
    
def details(request, restaurant_number=""):
    try:
        restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
    except restaurant.DoesNotExist:
        raise Http404('This restaurant is not avaliable in this moment')
    context = {
        'name' : restaurant.name,
        'restaurant_number' : restaurant.restaurant_number,
        'menu_description' : restaurant.menu_description,
        'price_average' : restaurant.price_average,
        'is_promot' : restaurant.is_promot,
        'rate' : restaurant.rate,
        'address' : restaurant.address,
        'city' : restaurant.city,
        'country' : restaurant.country,
        'featured_photo' : restaurant.featured_photo,
        'category' : restaurant.category
    }
    return render(request, 'forkilla/details.html', context)
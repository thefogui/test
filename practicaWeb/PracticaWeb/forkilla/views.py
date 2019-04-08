from django.shortcuts import render
from django.http import HttpResponse
from .models import Restaurant, ViewedRestaurants

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
        viewedrestaurants = _check_session(request)
        restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
        viewedrestaurants.restaurant.add(restaurant)
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
            'category' : restaurant.category,
            'viewedrestaurants' : viewedrestaurants
        }
    except Restaurant.DoesNotExist:
        raise Http404('This restaurant is not avaliable in this moment')
    return render(request, 'forkilla/details.html', context)
    
def reservation(request):
    try:
        if request.method == "POST":
            form = ReservationForm(request.POST)
            if form.is_valid():
                    resv = form.save(commit=False)
                    restaurant_number = request.session["reserved_restaurant"]
                    resv.restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
                    resv.save()
                    request.session["reservation"] = resv.id
                    request.session["result"] = "OK"

            else:
                  request.session["result"] = form.errors
            return HttpResponseRedirect(reverse('checkout'))

        elif request.method == "GET":
            restaurant_number = request.GET["reservation"]
            restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
            request.session["reserved_restaurant"] = restaurant_number

            form = ReservationForm()
            context = {
                'restaurant': restaurant,
                'viewedrestaurants': viewedrestaurants,
                'form': form
            }
    except Restaurant.DoesNotExist:
        return HttpResponse("Restaurant Does not exists")
    return render(request, 'forkilla/reservation.html', context)
    
def _check_session(request):

    if "viewedrestaurants" not in request.session:
        viewedrestaurants = ViewedRestaurants()
        viewedrestaurants.save()
        request.session["viewedrestaurants"] = viewedrestaurants.id_vr
    else:
        viewedrestaurants = ViewedRestaurants.objects.get(id_vr=request.session["viewedrestaurants"])
    return viewedrestaurants
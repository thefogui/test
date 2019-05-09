from django.shortcuts import render
from django.http import HttpResponse
from django.db.models import Q
from django.shortcuts import render_to_response
from .models import Restaurant, ViewedRestaurants, Review, Reservation, Comment
from .forms import ReservationForm, CommentsForm
from django.http import HttpResponseRedirect
from django.urls import reverse
from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.decorators import login_required
from django_comments.models import Comment # new

# Create your views here.
def index(request):
    categories = Restaurant.CATEGORIES
    restaurants_by_promoted = Restaurant.objects.filter(is_promot="True")
    context = {
        'categories' : categories,
        'restaurants' : restaurants_by_promoted
    }
    return render(request, 'forkilla/index.html', context)

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
        comments = []
        for comment in restaurant._meta.related_objects:
            comments.append(comment)
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
            'viewedrestaurants' : viewedrestaurants,
            'comments' : comments
        }
    except Restaurant.DoesNotExist:
        raise Http404('This restaurant is not avaliable in this moment')
    return render(request, 'forkilla/details.html', context)

def checkout(request):
    context = {}
    return render(request, 'forkilla/checkout.html', context)

@login_required
def reservation(request):
    try:
        if request.method == "POST":
            form = ReservationForm(request.POST)
            if form.is_valid():
                resv = form.save(commit=False)
                restaurant_number = request.session["reserved_restaurant"]

                ##checks if the disponibility of the restaurant for the selected time slot
                resv.restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
                if checkDisponibility(resv.restaurant, resv):
                    resv.save()
                    request.session["reservation"] = resv.id
                    request.session["result"] = "OK"
                else:
                    context = {
                        'error' : "Not enough capacity"
                    }
                    render(request, 'forkilla/checkout.html', context)
            else:
                  request.session["result"] = form.errors
            return HttpResponseRedirect(reverse('checkout'))

        elif request.method == "GET":
            restaurant_number = request.GET["reservation"]
            restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
            request.session["reserved_restaurant"] = restaurant_number
            viewedrestaurants = _check_session(request)

            form = ReservationForm()
            context = {
                'restaurant': restaurant,
                'viewedrestaurants': viewedrestaurants,
                'form': form
            }
    except Restaurant.DoesNotExist:
        return HttpResponse("Restaurant Does not exists")
    return render(request, 'forkilla/reservation.html', context)

@login_required
def add_comment(request):
    try:
        if request.method == "POST":
            form = CommentsForm(request.POST)
            if form.is_valid():
                post = form.save(commit=False)
                restaurant_number = request.session["reserved_restaurant"]
                return details(request, restaurant_number)
        else:
            form = CommentForm()
    except Exception:
        return HttpResponse("Restaurant Does not exists")
    return details(request, restaurant_number)

##checks if the disponibility of the restaurant for the selected time slot
def checkDisponibility(restaurant, reservation):
    cont = 0
    for i in Reservation.objects.filter(restaurant_id = restaurant.id).filter(time_slot = reservation.time_slot).filter(day=reservation.day):
        cont = cont + i.num_people
    if restaurant.restaurant_capacity >= reservation.num_people + cont:
        return True
    return False

def _check_session(request):
    if "viewedrestaurants" not in request.session:
        viewedrestaurants = ViewedRestaurants()
        viewedrestaurants.save()
        request.session["viewedrestaurants"] = viewedrestaurants.id_vr
    else:
        viewedrestaurants = ViewedRestaurants.objects.get(id_vr=request.session["viewedrestaurants"])
    return viewedrestaurants


def review(request):
    pass

def search_restaurant(request):
    """ Search for the GET called q and return the string """
    query = request.GET.get('q', '')

    if query:
        """ icontains accept upper caser and down caser"""
        qset = (
            Q(restaurant_number__icontains=query) |
            Q(name__icontains=query) |
            Q(city__icontains=query)
        )

        #distinct to get only one of the searched restaurant
        results = Restaurant.objects.filter(qset).distinct()
    else:
        results = []

    return render(request, "forkilla/search.html", {
        "results" : results,
        "query" : query
    })

def register(request):
    if request.method == 'POST':
        form = UserCreationForm(request.POST)
        if form.is_valid():
            new_user = form.save()
            return HttpResponseRedirect(reverse("index"))
    else:
        form = UserCreationForm()
    return render(request, "registration/register.html", {
        'form': form,
    })

from django.shortcuts import render
from django.http import HttpResponse, Http404
from django.db.models import Q
from django.shortcuts import render_to_response
from .models import Restaurant, ViewedRestaurants, ReviewRestaurant, Reservation, Comment, RestaurantInsertDate
from .forms import ReservationForm, CommentsForm, RateForm
from django.http import HttpResponseRedirect
from django.urls import reverse
from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.decorators import login_required
from django_comments.models import Comment # new

from django.contrib.auth.models import User, Group
from rest_framework import viewsets
from .serializers import RestaurantSerializer
from django.contrib.auth import authenticate, login
from django.contrib.auth.models import User
from rest_framework import generics
from .serializers import UserSerializer

from .permissions import UserHaveCommercialRole
from rest_framework import permissions

# Create your views here.
def index(request):
    
    restaurants = Restaurant.objects.order_by().values_list('city').distinct()
    restaurants_by_promoted = Restaurant.objects.filter(is_promot="True")
    viewedrestaurants = _check_session(request)
    
    categories = []
    for restaurant in restaurants:
        categories.append(restaurant[0])

    context = {
        'categories' : categories,
        'restaurants' : restaurants_by_promoted,
        'viewedrestaurants' : viewedrestaurants,
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
    
    viewedrestaurants = _check_session(request)
    
    categories = []
    restaurants = Restaurant.objects.order_by().values_list('city').distinct()
    for restaurant in restaurants:
        categories.append(restaurant[0])
    
    context = {
        'city': city,
        'restaurants': restaurants_by_city.order_by('category'),
        'promoted': promoted,
        'categories' : categories,
        'viewedrestaurants' : viewedrestaurants,
        'categories_filter' : Restaurant.CATEGORIES
    }
    return render(request, 'forkilla/restaurants.html', context)

def details(request, restaurant_number=""):
    try:
        
        viewedrestaurants = _check_session(request)
        restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
        lastviewed = RestaurantInsertDate(viewedrestaurants=viewedrestaurants,restaurant= restaurant)
        lastviewed.save()
        
        stars = 0
        reviews = 0
        
        for star in restaurant.reviews.all():
            stars = stars + star.stars
            reviews = reviews + 1
                
        form = CommentsForm()
        
        if reviews != 0:
            stars = int(stars / reviews)
        
        categories = []
        restaurants = Restaurant.objects.order_by().values_list('city').distinct()
        for r in restaurants:
            categories.append(r[0])
        
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
            'stars' : stars,
            'restaurant' : restaurant,
            'form' : form,
            'categories' : categories,
            
        }
    except Restaurant.DoesNotExist:
        raise Http404('This restaurant is not avaliable in this moment')
    return render(request, 'forkilla/details.html', context)

def checkout(request, context=""):
    categories = []
    
    restaurants = Restaurant.objects.order_by().values_list('city').distinct()
    for restaurant in restaurants:
        categories.append(restaurant[0])
    
    context['categories'] = categories
    
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
                resv.user = request.user
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
            context = {
                "reserva" : resv
            }
            return checkout(request, context)

        elif request.method == "GET":
            restaurant_number = request.GET["reservation"]
            restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
            request.session["reserved_restaurant"] = restaurant_number
            viewedrestaurants = _check_session(request)

            form = ReservationForm()
            
            categories = []
            restaurants = Restaurant.objects.order_by().values_list('city').distinct()
            for restaurant in restaurants:
                categories.append(restaurant[0])
            
            context = {
                'restaurant': restaurant,
                'viewedrestaurants': viewedrestaurants,
                'form': form,
                'categories' : categories
            }
    except Restaurant.DoesNotExist:
        return HttpResponse("Restaurant Does not exists")
    return render(request, 'forkilla/reservation.html', context)

@login_required
def add_comment(request, restaurant_number=""):
    restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
    try:
        if request.method == "POST":
            form = CommentsForm(request.POST)
            if form.is_valid():
                comment = form.save(commit=False)
                comment.the_restaurant = restaurant
                comment.user = request.user
                comment.save()
                return details(request, restaurant.restaurant_number)
        else:
            form = CommentForm()
        template = 'forkilla/details.html'
        context = { 'form' : form}
        return render(request, template, context)
    except Exception:
        return HttpResponse("Restaurant Does not exists")
    

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
    
@login_required
def review(request, restaurant_number=""):
    try:
        restaurant = Restaurant.objects.get(restaurant_number=restaurant_number)
        if request.method == 'POST':
            
            stars = request.POST['estrellas']
            user = request.user
            
            reviewRestaurant = ReviewRestaurant()
            reviewRestaurant.restaurant = restaurant
            reviewRestaurant.user = user
            reviewRestaurant.stars = stars
            
            if not ReviewRestaurant.objects.filter(restaurant=restaurant, user=user).exists():
                reviewRestaurant.save()
            
            return details(request, restaurant.restaurant_number)
        return details(request, restaurant.restaurant_number)
    except Exception:
        return render(request, 'forkilla/error.html')

def search_restaurant(request):
    """ Search for the GET called q and return the string """
    query = request.GET.get('q', '')

    if query:
        """ icontains accept upper caser and down caser"""
        qset = (
            Q(restaurant_number__icontains=query) |
            Q(name__icontains=query) |
            Q(category__icontains=query) |
            Q(city__icontains=query)
        )

        #distinct to get only one of the searched restaurant
        results = Restaurant.objects.filter(qset).distinct()
    else:
        results = []

    viewedrestaurants = _check_session(request)
    
    categories = []
    restaurants = Restaurant.objects.order_by().values_list('city').distinct()
    for restaurant in restaurants:
        categories.append(restaurant[0])

    return render(request, "forkilla/search.html", {
        "results" : results,
        "query" : query,
        "viewedrestaurants" : viewedrestaurants,
        'categories' : categories
    })

def register(request):
    if request.method == 'POST':
        form = UserCreationForm(request.POST)
        if form.is_valid():
            new_user = form.save()
            
            username = request.POST.get('username', "")
            password = request.POST.get('password', "")
            
            new_user = authenticate(username=form.cleaned_data['username'],
                                    password=form.cleaned_data['password1'])
            login(request, new_user)

            return HttpResponseRedirect(reverse("index"))
    else:
        form = UserCreationForm()
        
    categories = []
    restaurants = Restaurant.objects.order_by().values_list('city').distinct()
    for restaurant in restaurants:
        categories.append(restaurant[0])
    return render(request, "registration/register.html", {
        'form': form,
        'categories' : categories
    })

@login_required
def reservationlist(request):
    template = 'forkilla/reservationlist.html'
    categories = Restaurant.CATEGORIES
    user = request.user
    
    reservations = []
    
    reservationList = Reservation.objects.filter(user=user)    
    
    for reservation in reservationList:
        restaurant = Restaurant.objects.get(restaurant_number=reservation.restaurant.restaurant_number)
        reservations.append((reservation, restaurant))
        
    viewedrestaurants = _check_session(request)
    
    restaurants = Restaurant.objects.order_by().values_list('city').distinct()
    categories = []
    for restaurant in restaurants:
        categories.append(restaurant[0])
    
    context = { 
        "viewedrestaurants" : viewedrestaurants,
        'categories' : categories,
        'reservationList' : reservations
    }
    return render(request, template, context)
    
def deleteReservation(request, id=""):
    categories = Restaurant.CATEGORIES
    viewedrestaurants = _check_session(request)
    
    user = request.user
    template = 'forkilla/deleteReservation.html'
    context = { 
        "viewedrestaurants" : viewedrestaurants,
        'categories' : categories,
    }
    return render(request, template, context)
    
class RestaurantViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows Restaurants to be viewed or edited.
    """
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, UserHaveCommercialRole,)
    serializer_class = RestaurantSerializer
    
    def get_queryset(self):
    
        queryset = Restaurant.objects.all().order_by('restaurant_number')
        
        category = self.request.query_params.get('category', None)
        
        if category:
            queryset = queryset.filter(category=category)
            
        city = self.request.query_params.get('city', None)
        
        if city:
            queryset = queryset.filter(city=city)
            
        price = self.request.query_params.get('price', None)
        
        if price:
            queryset = queryset.filter(price_average__level__lte=price)
            
        return queryset
    

class UserList(generics.ListAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer

class UserDetail(generics.RetrieveAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer
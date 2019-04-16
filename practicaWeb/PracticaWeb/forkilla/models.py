from django.db import models
from datetime import datetime
from django.core.validators import MinValueValidator

#########################################################
#   Every time this class is changed follow these steps:#
#                                                       #
#   1. Change your models (in models.py).               #
#   2. Run python manage.py makemigrations to create    #
#      migrations for those changes                     #   
#   3. Run python manage.py migrate to apply those      #
#      changes to the database.                         #
#########################################################

# To load fixture data: python manage.py loaddata <fixturename>

# Create your models here.
class Restaurant(models.Model):
    CATEGORIES = (
        ("Rice", "Rice"),
        ("Fusi", "Fusion"),
        ("BBQ", "Barbecue"),
        ("Chin", "Chinese"),
        ("Medi","Mediterranean"),
        ("Crep","Creperie"),
        ("Hind","Hindu"),
        ("Japa","Japanese"),
        ("Ital","Italian"),
        ("Mexi","Mexican"),
        ("Peru", "Peruvian"),
        ("Russ","Russian"),
        ("Turk","Turkish"),
        ("Basq","Basque"),
        ("Vegy", "Vegetarian"),
        ("Afri","African"),
        ("Egyp","Egyptian"),
        ("Grek","Greek")
    )
    _d_categories = dict(CATEGORIES)

    restaurant_number = models.CharField(max_length=8, unique=True)
    name = models.CharField(max_length=50)
    menu_description = models.TextField()
    price_average = models.DecimalField(max_digits=5, decimal_places=2)
    is_promot = models.BooleanField()
    rate = models.DecimalField(max_digits=3, decimal_places=1)
    address = models.CharField(max_length=50)
    city = models.CharField(max_length=50)
    country = models.CharField(max_length=50)
    featured_photo = models.ImageField()
    category = models.CharField(max_length=5, choices=CATEGORIES)
    restaurant_capacity = models.PositiveIntegerField()

    def get_human_category(self):
        return self._d_categories[self.category]

    def __str__(self):
        return ('[**Promoted**]' if self.is_promot else '') + "[" + self.category + "] " \
                "[" + self.restaurant_number + "] " + self.name + " - " + self.menu_description + " (" + str(self.rate) + ")" \
                ": " + str(self.price_average) + u" €"
                
class Reservation(models.Model):
    SLOTS = (
        ("morning_first", "12h00"),
        ("morning_second", "13h00"),
        ("morning_third", "14h00"),
        ("morning_fourth", "15h00"),
        ("evening_first", "20h00"),
        ("evening_second", "21h00"),
        ("evening_third", "22h00"),
    )
    _d_slots = dict(SLOTS)
    id = models.AutoField(primary_key=True)
    restaurant = models.ForeignKey(Restaurant, on_delete=models.PROTECT)
    day = models.DateField(default=datetime.now)
    time_slot = models.CharField(max_length=15, choices=SLOTS)
    num_people = models.PositiveIntegerField(default=1, validators=[MinValueValidator(1)])

    def get_human_slot(self):
        return self._d_slots[self.time_slot]
        
class ViewedRestaurants(models.Model):
    id_vr = models.AutoField(primary_key=True)
    restaurant = models.ManyToManyField(Restaurant)
        
class Review(models.Model):
    reviews_id = models.CharField(max_length=10, unique=True)
    restaurant_number = models.CharField(max_length=8)
    message = models.TextField()
    review_stars = models.PositiveIntegerField()
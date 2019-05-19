from django.db import models
from datetime import datetime
from django.core.validators import MinValueValidator

from django.contrib.auth.models import User

from django_comments.moderation import CommentModerator, moderator

from pygments.lexers import get_lexer_by_name
from pygments.formatters.html import HtmlFormatter
from pygments import highlight

from .global_vars import ROLES


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
    featured_photo = models.ImageField(upload_to ='images/', default='images/1.jpeg')
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
    user = models.CharField(max_length=200, default="Anonymous User")

    def get_human_slot(self):
        return self._d_slots[self.time_slot]
        
    def __str__(self):
        return "Reservation at: " + self.restaurant.name + " on " + str(self.day) + " for " + str(self.num_people) + " people, on " + str(self.time_slot)
        
class ViewedRestaurants(models.Model):
    id_vr = models.AutoField(primary_key=True)
    restaurant = models.ManyToManyField(Restaurant)
    
class RestaurantInsertDate(models.Model):
    viewedrestaurants = models.ForeignKey(ViewedRestaurants, on_delete=models.CASCADE)
    restaurant = models.ForeignKey(Restaurant, on_delete=models.CASCADE)
    date_added = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-date_added']
        
class ReviewRestaurant(models.Model):
    restaurant = models.ForeignKey(Restaurant, on_delete=models.CASCADE, null=True, related_name='reviews')
    user = models.CharField(max_length=200, default="Anonymous User")
    stars = models.PositiveIntegerField(default=0)
    
class Comment(models.Model):
    the_restaurant = models.ForeignKey('Restaurant', on_delete=models.CASCADE, null=False, related_name='comments')
    user = models.CharField(max_length=200, default="Anonymous User")
    posted_date = models.DateTimeField(auto_now_add=True)
    content = models.TextField()
    approved_comment = models.BooleanField(default=False)
    
    def approved(self):
        self.approved_comment = True
        self.save()
        
    def __str__(self):
        return self.content
        
class Snippet(models.Model):
    owner = models.ForeignKey('auth.User', related_name='snippets', on_delete=models.CASCADE)
    highlighted = models.TextField()
    
    def save(self, *args, **kwargs):
        lexer = get_lexer_by_name(self.language)
        linenos = 'table' if self.linenos else False
        options = {'title': self.title} if self.title else {}
        formatter = HtmlFormatter(style=self.style, linenos=linenos,
                                  full=True, **options)
        self.highlighted = highlight(self.code, lexer, formatter)
        super(Snippet, self).save(*args, **kwargs)
        
class UserRole(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    role = models.CharField(max_length=15, choices=ROLES)
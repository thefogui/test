from .models import Restaurant, Snippet
from rest_framework import serializers
from django.contrib.auth.models import User

class RestaurantSerializer(serializers.HyperlinkedModelSerializer):
    
    class Meta:
        model = Restaurant
        fields = (
        'restaurant_number', 'name', 'menu_description', 
	'price_average', 'is_promot', 'rate', 'address', 
	'city', 'country', 'featured_photo', 'category', 
	'restaurant_capacity')
    


class UserSerializer(serializers.ModelSerializer):
    snippets = serializers.PrimaryKeyRelatedField(many=True, queryset=Snippet.objects.all())

    class Meta:
        model = User
        fields = ('id', 'username', 'snippets')
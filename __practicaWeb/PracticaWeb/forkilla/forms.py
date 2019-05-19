from django import forms

from .models import Reservation, Comment, ReviewRestaurant

class ReservationForm(forms.ModelForm):

    class Meta:
        model = Reservation
        fields = ["day", "time_slot", "num_people"]
        
class CommentsForm(forms.ModelForm):
    class Meta:
        model = Comment
        fields = ["content", ]
        
class RateForm(forms.ModelForm):
    class Meta:
        model = ReviewRestaurant
        fields = ('stars', )
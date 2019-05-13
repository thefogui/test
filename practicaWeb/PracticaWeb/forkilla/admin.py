from django.contrib import admin
from .models import Restaurant, Comment, ReviewRestaurant, Reservation

class CommentAdmin(admin.ModelAdmin):
    list_display = ('user', 'approved')

admin.site.register(Restaurant)
admin.site.register(Reservation)
admin.site.register(ReviewRestaurant)
admin.site.register(Comment, CommentAdmin)
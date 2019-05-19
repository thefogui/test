from django.contrib import admin
from .models import Restaurant, Comment, ReviewRestaurant, Reservation, UserRole
from django.contrib.auth.models import User
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin

class UserRoleInline(admin.StackedInline):
    model = UserRole
    can_delete = False
    verbose_name_plural = 'user_roles'

class CommentAdmin(admin.ModelAdmin):
    list_display = ('user', 'approved')

class UserAdmin(BaseUserAdmin):
    inlines = (UserRoleInline, )

admin.site.unregister(User)
admin.site.register(User, UserAdmin)

admin.site.register(Restaurant)
admin.site.register(Reservation)
admin.site.register(ReviewRestaurant)
admin.site.register(Comment, CommentAdmin)
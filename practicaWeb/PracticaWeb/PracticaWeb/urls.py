"""PracticaWeb URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from django.conf.urls import include, url
from django.contrib.auth import views as v
from django.conf import settings
from django.conf.urls.static import static
from django.conf.urls import url, include
from rest_framework import routers
from forkilla import views

router = routers.DefaultRouter()
router.register(r'restaurants', views.RestaurantViewSet, base_name="Restaurant")


# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browsable API.

urlpatterns = [
    path('admin/', admin.site.urls),
    url(r'^forkilla/', include('forkilla.urls')),
    url(r'', include('forkilla.urls')),
    url(r'^accounts/login/$', v.LoginView.as_view(), name='login'),
    url(r'^accounts/logout/$', v.LogoutView.as_view(), {'next_page': '/forkilla'}, name='logout'),
    url(r'^api/', include(router.urls)),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),
    path('users/', views.UserList.as_view()),
    path('users/<int:pk>/', views.UserDetail.as_view()),
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)

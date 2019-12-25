from django.urls import path

from . import views


app_name = 'network'
urlpatterns = [
    path('', views.index, name='index'),
    path('get_graph', views.get_graph, name='get_graph'),
    path('send_msg', views.send_msg, name='send_msg'),
]

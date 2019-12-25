import json

from .network import Network
from django.http import HttpResponse, JsonResponse
from django.shortcuts import render


net = None

def index(request):
    return render(request, 'network/index.html')

def get_graph(request):
    global net
    net = Network()
    res = {'nodes': [], 'edges': []}
    
    for node in net.nodes:
        res['nodes'].append({
            'id': node.id, 
            'channels': [chan.id for chan in node.channels],
            'type': node.type,
            'routes': [{'to':key, 
                        'length': route['length'], 
                        'route': route['route']} for key, route in node.routes.items()]
        })
    
    for chan in net.channels:
        res['edges'].append({
                'id': chan.id,
                'weight': chan.weight,
                'nodes': [chan.nodes[0].id, chan.nodes[1].id],
                'type': chan.type
            })
    return JsonResponse(res)

def send_msg(request):
    src = int(request.POST['src'])
    msg = {
        'msg_size': int(request.POST['msg_size']),
        'pack_size': int(request.POST['pack_size']),
        'ctrl_size': int(request.POST['ctrl_size']),
    }
    data = net.send(src, msg)
    return JsonResponse(data)

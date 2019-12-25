var network, nodes, edges, vis_nodes, vis_edges 

$(document).ready(function(){
    $.get('get_graph', function(data, status){
        nodes = data.nodes
        edges = data.edges

        nodes.forEach(function(value) {
            vis_nodes.add({
                id: value.id, 
                label: value.id.toString(),
                // group: value.type
            })
        })

        edges.forEach(function(value) {
            dashes = false
            width = 1
            if (value.type == "satellite")
                dashes = true

            vis_edges.add({
                id: value.id,
                from: value.nodes[0],
                to: value.nodes[1],
                dashes: dashes,
                // label: value.weight.toString(),
                // font: {align: 'middle'}
            })
        })
    })
})


vis_nodes = new vis.DataSet([]);
vis_edges = new vis.DataSet([]);

var container = document.getElementById('mynetwork');

var data = {
    nodes: vis_nodes,
    edges: vis_edges
};
options = {
    nodes: {
        color: '#999966',
        shape: 'circle'
    },
    groups: {
        0: {colot: '#999966'},
        // 1: {color: '#686843'},
    },
    edges: {
        smooth: {
            enabled: false,
            roundness: 0.3,
        },
    },
    interaction: {
        hover: false
    },
};
network = new vis.Network(container, data, options);


var info = new Vue({
    el: "#info",
    delimiters: ["${", "}"],
    data: {
        show_node: false,
        show_all: false,
        id: 0,
        routes: {},
        node_type: 0,

        show_chan: false,
        weight: 0,
        type: 'duplex',
        nodes: [],

        show_send: false,
        msg_size: 1500,
        pack_size: 400,
        ctrl_size: 48,

        show_res: false,
        results: {}
    },
    methods: {
        send: function(e){
            e.preventDefault()
            data = {
                src: this.id, 
                msg_size: this.msg_size,
                pack_size: this.pack_size,
                ctrl_size: this.ctrl_size,
            }
            $.ajax({
                url: "send_msg", 
                headers: {'X-CSRFToken': $.cookie('csrftoken')},
                type: "post",
                dataType: "json",
                data: data,
                success: function(data){
                    info.show_res = true
                    info.show_all = false
                    info.results = data
                },
            })
        },
    }
})



network.on("selectNode", function(params){
    info.show_node = true
    info.show_chan = false
    info.id = params.nodes[0]
    info.node_type = nodes[info.id].type
    info.routes = nodes[info.id].routes
})

network.on("selectEdge", function(params){
    if (params.nodes.length != 0)
        return

    info.show_chan = true
    info.id = params.edges[0]
    var edge = edges[info.id]
    info.weight = edge.weight
    info.nodes = edge.nodes
    info.type = edge.type
})

network.on("deselectNode", function(params){
    info.show_node = false
    info.show_send = false
    info.show_res = false
})

network.on("deselectEdge", function(params){
    info.show_chan = false
})

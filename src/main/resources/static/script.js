

function init() {
  console.log("Initializing");
  createGraph()
  //  setTimeout(updateData, 500);
}

function crd_menu(element, crd) {
  var label = document.createElement('label');
  label.setAttribute('class', 'list-group-item d-flex gap-2');

  var input = document.createElement('input');
  input.setAttribute('class', 'form-check-input flex-shrink-0');
  input.setAttribute('type', 'checkbox');
  input.setAttribute('checked', '1');
  input.setAttribute('onclick', 'updateCheckboxNode(this);');
  input.setAttribute('id', 'cb_' + element)

  var span = document.createElement('span');
  span.innerHTML = element;

  label.appendChild(input)
  label.appendChild(span)

  crd.appendChild(label)
}

// called onclick of toppings checkboxes
function updateCheckboxNode(e) {
  console.log("Update Checkbox Node " + e)
  console.log(e.getAttribute('checked'));
  if (e.getAttribute('checked') == 1) {   
    e.setAttribute('checked', '0');
  } else {   
    e.setAttribute('checked', '1');
  }
}


function createGraph() {
  console.log("Creating graph");

  fetch('api/kpack').then(res => res.json()).then(data => {

    const crds = new Set();
    data.nodes.forEach(node => crds.add(node.kind))

    crd = document.getElementById('crd')
    while (crd.firstChild) {
      crd.removeChild(crd.lastChild);
    }
    Array.from(crds).sort().forEach(element => crd_menu(element, crd));

    const highlightNodes = new Set();
    const highlightLinks = new Set();

    const Graph = ForceGraph()
      (document.getElementById('graph'))

      .linkDirectionalArrowLength(1)
      .nodeId('id')
      .nodeAutoColorBy('group')
      .nodeVisibility((node) => {
        e = document.getElementById('cb_' + node.kind)
        if (e == null)
          return true;
        else
          return e.getAttribute('checked') == 1
      })
      .linkVisibility((link) => {
        source_node = document.getElementById('cb_' + link.source.kind)
        target_node = document.getElementById('cb_' + link.target.kind)
        if (source_node == null || target_node == null)
          return true;
        else
          return target_node.getAttribute('checked') == 1 && source_node.getAttribute('checked') == 1

      })
      .nodeCanvasObject((node, ctx, globalScale) => {
        const label = node.id;
        const fontSize = 16 / globalScale;

        const textWidth = ctx.measureText(label).width;
        const bckgDimensions = [textWidth, fontSize].map(n => n + fontSize * 0.3); // some padding

        ctx.fillStyle = 'rgba(255, 255, 255, 0.8)';
        ctx.font = `${fontSize}px Sans-Serif`;

        if (node.ready) {
          ctx.fillRect(node.x - bckgDimensions[0] / 2, node.y - bckgDimensions[1] / 2, ...bckgDimensions);
        } else {
          ctx.strokeStyle = node.color
          ctx.strokeRect(node.x - bckgDimensions[0] / 2, node.y - bckgDimensions[1] / 2, ...bckgDimensions)
        }

        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillStyle = node.color;
        ctx.fillText(label, node.x, node.y);

        node.__bckgDimensions = bckgDimensions; // to re-use in nodePointerAreaPaint
        highlightNodes.has(node) ? 'before' : undefined
      })
      .nodePointerAreaPaint((node, color, ctx) => {
        ctx.fillStyle = color;
        const bckgDimensions = node.__bckgDimensions;
        bckgDimensions && ctx.fillRect(node.x - bckgDimensions[0] / 2, node.y - bckgDimensions[1] / 2, ...bckgDimensions);
      })
      //.onNodeClick(node => window.open(`/details/${node.kind}/${node.id}`))
      .linkDirectionalArrowLength(3)
      .linkDirectionalArrowRelPos(1)
      .linkCurvature(d =>
        0.07 * // max curvature
        //   curve outwards from source, using gradual straightening within a margin of a few px
        Math.max(-1, Math.min(1, (d.source.x - d.target.x) / 5)) *
        Math.max(-1, Math.min(1, (d.target.y - d.source.y) / 5))
      )
      .cooldownTicks(15)
      .graphData(data)
      .onNodeHover(node => {
        highlightNodes.clear();
        highlightLinks.clear();
        if (node) {
          highlightNodes.add(node);
          let { nodes, links } = Graph.graphData();
          links.forEach(blink => {
            if (blink.source.id == node.id) {
              highlightLinks.add(blink);
            }
          })
        }
        hoverNode = node || null;
      })
      .onLinkHover(link => {
        highlightNodes.clear();
        highlightLinks.clear();

        if (link) {
          highlightLinks.add(link);

          let { nodes, links } = Graph.graphData();
          links.forEach(blink => {
            if (blink.source.id == link.target.id) {
              highlightLinks.add(blink);
            }
          })

          //highlightNodes.add(link.source);
          //highlightNodes.add(link.target);
        }
      })
      .autoPauseRedraw(false) // keep redrawing after engine has stopped
      .linkWidth(link => highlightLinks.has(link) ? 5 : 1)
      .linkDirectionalParticles(4)
      .linkDirectionalParticleWidth(link => highlightLinks.has(link) ? 4 : 0)


    Graph.d3Force('center', null);

    // fit to canvas when engine stops
    Graph.onEngineStop(() => Graph.zoomToFit(1000));
  });


}

function updateData() {
}
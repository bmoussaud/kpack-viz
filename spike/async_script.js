

function init() {
  console.log("Initializing");
  createGraph()
//  setTimeout(updateData, 500);
  //updateData();
}

function createGraph() {

  const highlightNodes = new Set();
  const highlightLinks = new Set();

  const Graph = ForceGraph()
    (document.getElementById('graph'))
    .nodeId('id')
    .nodeAutoColorBy('group')
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
    //.cooldownTicks(10)
    //.autoPauseRedraw(false) // keep redrawing after engine has stopped     
    .linkWidth(link => highlightLinks.has(link) ? 5 : 1)
    .linkDirectionalParticles(4)
    .linkDirectionalParticleWidth(link => highlightLinks.has(link) ? 4 : 0)
   
  setInterval(() => {
    console.log("Updating data....");
    fetch('datasets/kpack4.json').then(res => res.json()).then(data => {
      //Graph.pauseAnimation()
      Graph.graphData(data);
      Graph.resumeAnimation()
    })}, 1000);
  }
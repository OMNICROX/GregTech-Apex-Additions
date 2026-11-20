// Insanely CrazyCraft: /killhostiles [radius]
// Kills every hostile mob (vanilla + modded) within the radius of whoever runs it (default 128 blocks).
// Mobs with a custom name tag are spared. Operators only.

ServerEvents.commandRegistry(event => {
  let { commands: Commands, arguments: Arguments } = event

  let run = (ctx, radius) => {
    let src = ctx.source
    let level = src.level
    let p = src.position
    let r2 = radius * radius
    let n = 0
    level.entities.forEach(e => {
      if (!e.monster || e.player) return
      if (e.customName) return
      let dx = e.x - p.x(), dy = e.y - p.y(), dz = e.z - p.z()
      if (dx * dx + dy * dy + dz * dz > r2) return
      e.discard()
      n++
    })
    let msg = `Removed ${n} hostile mob(s) within ${radius} blocks.`
    src.sendSuccess(() => Text.of(msg), true)
    return n
  }

  event.register(
    Commands.literal('killhostiles')
      .requires(s => s.hasPermission(2))
      .executes(ctx => run(ctx, 128))
      .then(Commands.argument('radius', Arguments.INTEGER.create(event))
        .executes(ctx => run(ctx, Math.max(1, Arguments.INTEGER.getResult(ctx, 'radius')))))
  )
})

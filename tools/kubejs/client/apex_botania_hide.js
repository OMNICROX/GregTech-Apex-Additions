// Apex Ascension - Botania items that are switched off (apex_botania.js): Orechid / Orechid Ignem skip GT ore veins,
// the Elven Mana Fluxfield turns mana into FE (free GT power).
JEIEvents.hideItems(event => {
  ;['botania:orechid', 'botania:floating_orechid', 'botania:orechid_ignem', 'botania:floating_orechid_ignem',
    'botanicadds:elven_fluxfield'].forEach(id => { try { event.hide(id) } catch (e) {} })
})

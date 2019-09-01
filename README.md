# Recycler

## Minecraft mod that allows you to recycle your damaged tools and armor in the blast furnace.

---

### This mod allows you do melt down tools and armor back to materials in the blast funace.

![img](docs/img/blast-pickaxe.png)
![img](docs/img/blast-horse-armor.png)
![img](docs/img/blast-sword.png)
![img](docs/img/blast-leggings.png)

---

### The amount of materials you get back depends on durability.

![img](docs/img/blast-damaged-1.png)
![img](docs/img/blast-damaged-2.png)
![img](docs/img/blast-damaged-3.png)
![img](docs/img/blast-damaged-4.png)

---

### All these items can be recycled in the blast furnace by default.

![img](docs/img/items-iron.png)
![img](docs/img/items-gold.png)
![img](docs/img/items-diamond.png)

---

### A diamond nugget is added so that you can melt down damaged diamond equipment.

![img](docs/img/craft-diamond.png)
![img](docs/img/craft-diamond-nuggets.png)

---

### Be careful! If you melt your equipment in the regular furnace you still get only a nugget back.

![img](docs/img/smelt-axe.png)

---

### More recipes can be added via data packs.

Sample recipe json:

```json
{
  "type": "recycler:blasting_recycling",
  "ingredient": {
    "item": "minecraft:iron_pickaxe"
  },
  "results": [
    "minecraft:iron_ingot",
    {
      "item": "minecraft:iron_nugget",
      "count": 9
    }
  ],
  "max_output": 3
}

```

`max_output` is the amount you get back when the item is not damaged at all.

The item from `results` that has the lowest `count` will be tried first and if `max_output × count × durability_percentage` is less than `1` the item with the next lowest `count` will be tried.

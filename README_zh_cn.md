Scott's Essential (SCE) Scott的服务器实用工具

这是一个对**服务器友好**的**Forge模组**， 添加了许多实用的指令以及一些服务（例如实体清理）。

如果您发现了任何bug或者觉得我的mod哪里需要改进，请**一定**通过任意方式联系我！谢谢！

我的邮箱：[ScottCTD@outlook.com](mailto:ScottCTD@outlook.com)

当然，直接在Github提交issue是个很好的选择。



插件的所有配置都在每个存档的serverconfig文件夹里，这个文件夹里的配置可以动态更新。也就是说，你改完配置之后保存一下，配置在服务端会自动更新，不用重启整个端。

当然，有一些特殊的配置，比如说指令的开关和指令的自定义名称，这些配置无法立即生效，必须在游戏里输入/reload重载之后才能生效。（当然你愿意重启一次端也是可以的。）


**指令 (所有的命令都可自定义 /home 可以改成 /h)**

- /scessential
  - /scessential
    - 用途: 打印当前模组的版本。
  - /scessential getRegistryName item
    - 用途: 获取你主手上的物品注册名 (例如 minecraft:stone)，左键点击弹出的消息可以复制注册名，注册名可以用来添加实体清理的白名单。
    - 权限：等级2（这是原版的权限系统，未来会加入更加完善的权限系统）
    - ![img](https://attachment.mcbbs.net/forum/202101/11/162735s2poupu654p0ubca.png)
  - /scessential getRegistryName mob
    - 用途: 获取你附近（在一个可配置的半径以内）的所有能动的实体的注册名。（可以直接点击复制）
    - 权限：等级2（这是原版的权限系统，未来会加入更加完善的权限系统）
- /spawn
  - 用途: 传送到世界的出生点
- /back
  - 用途: 传送到上一次传送之前所在的地方。
- /tpa
  - /tpa [目标玩家]
    - 用途: 给目标玩家发送一个传送请求。
  - /tpahere [目标玩家]
    - 用途: 给目标玩家发送一个传送请求，让目标玩家拆送到你身边。
  - /tphere [目标玩家]
    - 用途: 强制传送目标玩家到你身边。
    - 权限：等级2
  - /tpallhere
    - 用途: 传送所有的玩家到你身边。
  - /tpaaccept
    - 用途: 用于接受传送请求（玩家不必输入此指令，传送请求是可交互的，只需点一下同意即可）。
  - /tpadeny
    - 用途: 用于拒绝传送请求（玩家不必输入此指令，传送请求是可交互的，只需点一下同意即可）。
- /home
  - /home [家的名字]
    - 用途: 传送到你的某个家（家的名字可以自动补全）。
  - /sethome [家的名字]
    - 用途: 在你的位置设置一个家。
  - /delhome = /removehome [家的名字]
    - 用途: 删除一个家。
  - /listhomes
    - 用途: 列出你所有的家，你可以点击任意一个来传送到那个家。
    - ![img](https://attachment.mcbbs.net/forum/202101/11/162633htmy1itrp8gzrr66.png)
  - /homeother [目标玩家] [家的名字]
    - 用途: 传送到目标玩家的某个家。
    - 权限：等级2
  - /delotherhome = /removeotherhome [目标玩家] [家的名字]
    - 用途: 删除目标玩家的某个家。
    - 权限：等级2
  - /listotherhomes [目标玩家]
    - 用途: 列出目标玩家的所有家。
    - 权限：等级2
- /warp
  - /warp [传送点名称]
    - 用途: 传送到一个传送点。
  - /setwarp [传送点名称]
    - 用途: 设置一个传送点。
    - 权限：等级2
  - /delwarp [传送点名称]
    - 用途: 删除一个传送点。
    - 权限：等级2
  - /listwarps
    - 用途: 列出所有的传送点。
- /rtp
  - 用途: 随机传送你到一个本世界安全的位置。
- /fly
  - /fly [目标玩家]
    - 用途: 让一个玩家永久飞行。
    - 权限：等级2
  - /fly [目标玩家] [分钟]
    - 用途: 让一个玩家飞行几分钟。
    - 权限：等级2
- /hat
  - /hat
    - 用途: 把你现在主手的物品戴在头上。
  - /hat [目标玩家]
    - 用途: 把目标玩家主手上的物品戴在他的头上。
    - 权限：等级2
- /invsee [目标玩家]
  - 用途: 打开一个GUI，里面包含了目标玩家所有的物品（包括装备和副手）。
  - 权限：等级2
- /trashcan
  - 用途: 打开一个垃圾桶GUI，你可以点击GUI中的清空按钮以清空垃圾桶，当然，垃圾桶也会定时清除，这个定时也是可配置的。
  - ![img](https://attachment.mcbbs.net/forum/202101/11/162959p7ht5nh7zj7zfkr8.png)
- /rank
  - 用途: 打开一个GUI，里面显示所有玩家的排名 (E.g 死亡排名...)
  - ![img](https://attachment.mcbbs.net/forum/202101/11/162916e8b8mzjbwzs8qbjv.png)
- 未来可能会有更多的指令

**服务**

- 实体清理
  - 这是一个拥有非常高的可配置性的实体清理功能。
  - 白名单是由实体的注册名组成，可以通过/scessential getRegistryName item/mob 来获取物品或者其他实体的注册名。
  - 可以清理画，盔甲架，潜影贝的子弹（这玩意贼卡服）等实体。

**关于**

作者：ScottCTD ([ScottCTD@outlook.com](mailto:ScottCTD@outlook.com))

你可以修改我的mod，但是修改后的版本只能用于私人使用。

这个mod的制作灵感来自FTB Essential，但是由于FTBESS很长时间没更新了，他的功能有些缺失，细节也不够到位。

所以我就花了十天写了这个233
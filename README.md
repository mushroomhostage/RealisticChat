CakeMushroom's RealisticChat - realistic local chat!

*a collaboration of cakenggt and mushroomhostage*

***New! [RealisticChat 1.1](http://dev.bukkit.org/server-mods/realisticchat/files/3-realistic-chat-1-1/)*** - **released 2012/03/27, adds directional bullhorns, global chat, more configurability – 1.2.4 development builds supported**

Features:

* Local chat 
* Yelling
* Whispering
* Bullhorns
* Ear trumpets
* Walkie-talkies
* Global chat prefix

## Basic Usage
Talk normally. You'll only be able to clearly hear other players within 25 meters.

Beyond 25 up to 50 meters, chat will break up: letters will randomly drop from your
messages, with increasing probability the greater the distance.

To reach someone further away, you can **yell**. For example: "*help!*" adds 10 m. Multiple exclamation
marks will make you yell louder: "*help!!!!*" adds 500 m. Yelling will also make garbled messages clearer.
However, yelling comes at the cost of decreasing your food level!

If you want to talk to people very close by but hide your chat from others further away, you
can decrease your chat range by **whispering**, for example: "*(lets go)*". Only players within
10 m will be able to hear your whispering, and only those within 5 m will be able to hear you clearly.

## Advanced Usage

To further increase your chat range, speak while holding a **bullhorn** (diamond). This will double your
range within a conical region in front of you, just like a real bullhorn (or megaphone).
Bullhorns can be used in conjunction with yelling. Additionally, players hearing messages from bullhorns
will recognize the direction it is coming from -- you can use this ability for [acoustic location](http://en.wikipedia.org/wiki/Acoustic_location)
if you get lost.

For stealthily listening in for covert operations, wear an **ear trumpet**. A precursor from the 1600s
to the modern hearing aid, the [ear trumpet](http://en.wikipedia.org/wiki/Ear_trumpet) can increase
your hearing range significantly, but has no effect on your speaking range, allowing you to eavesdrop
on enemy bases from a safe distance while talking casually as normal with your partners in crime.
Craft it in the shape of a helmet with a diamond in the center, out of wooden planks (+100 m), leather (+150 m), or iron (+400 m),
then wear it on your head as armor for improved hearing. It has the same durability as a Golden Helmet.

For long-range communication, use a **walkie talkie** (compass). Hold the compass in your *hand*
and speak as normal, and your voice will be clearly transmitted through the device up to 1000 meters
(and break up beyond 1000 meters, up to 2000 meters)
to other players holding a compass anywhere in their *hotbar*. Walkie Talkies are especially
useful for keeping in touch with other players while travelling.

For private one-to-one communication, use a **smartphone** (clock). Hold the clock in your *hand*,
and use speak into it the name of a player you want to call. Its high-tech *voice activated calling*
capabilities will cause the device to ring up the player, as long as they are also holding a 
smartphone. Once the call is established anything said will also be transferred over the air
to the other player. You can hang up the call by moving your hand to another item. Note that 
other players can overhear you if they are nearby, just as if you are talking
normally, so be sure to go somewhere far away or use whispering if you really want your
conversation to be private (but this feature can be disabled if desired, see below). Smartphones
are very primitive at the moment, please report any problems or suggested enhancements.

All distances, items, and other parameters are configurable to your liking; see below.

## Commands and Permissions
None

## Configuration
**speakingRangeMeters** (50.0): Maximum distance between players to allow any chat message to be received,
under normal conditions. Note that the message may be garbled at longer distances, see below.

**garbleRangeDivisor** (2.0): Fraction of speakingRangeDistance after which the chat is garbled 
(letters randomly dropped with increasing probability further away). For example, 2.0 means beyond 1/2.0 = 
half of the maximum hearing range (default 50/2 = 25 m), chat will be garbled.

**garblePartialChance** (0.10): When a character is about to be dropped due to garbling, use this
probability to determine if the character should instead be dimmed (colored chatDimMessageColor), representing a barely audible piece of
the message. Set to 1.0 to always dim letters instead of dropping, or 0.0 to disable dimming entirely.

**garbleAllDroppedMessage** (~~~): Message replacement to display if you were unlucky enough for your
entire message to be garbled. Set to null to disable replacement.

**chatLineFormat** (%1$s: %2$s): The format of the chat line to be shown to the player. The first argument
is replaced with the player's display name, and the second with the message text. For the traditional
chat display, try "<%1$s> %2$s", or to hide player names for anonymous chat, use just "%2$s".

**chatSpokenPlayerColor** (YELLOW): Color of player name shown to a player when they themselves speak.

**chatHeardPlayerColor** (GREEN): Color of player name of speech heard from other players.

**chatMessageColor** (WHITE): Color of chat messages.

**chatDimMessageColor** (DARK\_GRAY): Color of partially-received characters, garbled by garblePartialChance.
Set to "MAGIC" for an interesting effect.

**yellMax** (4): Maximum exclamation marks allowed at end of message to increase yelling level.

**yell.#.hunger** (1, 2, 4, 20): Hunger (in half-drumsticks) depleted for each yelling level 
(number of trailing exclamation marks). 20 depletes all hunger.

**yell.#.rangeIncrease** (10.0, 50.0, 100.0, 500.0): Range increase in meters, beyond the normal range, for
each yelling level.

**whisperRangeDecrease** (40.0): Distance to decrease the chat range when whispering (parenthesized chat).
For example, 40.0 will decrease the normal 50.0 range to 10.0 meters maximum. The garble range divisor still
applies, so (by default) only those 10.0/2 = 5.0 meters away will hear you clearly.

**bullhornEnable** (true): Enable or disable bullhorns. 

**bullhornItem** (diamond): The hand-held item to use to represent bullhorns.

**bullhornFactor** (2.0): Multiply chat range by this value when player is holding a bullhorn (diamond).

**bullhornWidthDegrees** (70.0): Width of the bullhorn conical region in front of the player, in degrees.
70 is the default Minecraft field-of-view.

**bullhornChatLineFormat** ("%1$s [%3$s]: %2$s"): Messages received from bullhorns will be formatted
using this format string, with the third field replaced by the compass direction from where the message was
received. See also chatLineFormat.

**earTrumpetEnable** (true): Enable or disable ear trumpets.

**earTrumpetEnableCrafting** (true): Enable or disable crafting of ear trumpets.

**earTrumpet.#.rangeIncrease** (100.0, 150.0, 400.0): Hearing range increase for each of the tiers of ear trumpets.
The wearer will be able to hear (but not speak) up to this increased range.

**walkieEnable** (true): Enable or disable walkie talkies.

**walkieItem** (compass): The hand-held item to use to represent walkie talkies.

**walkieRangeMeters** (2000.0): The maximum range of walkie talkies. Note that near the end of this range,
messages will be garbled depending on walkieGarbleDivisor.

**walkieGarbleDivisor** (2.0): Fraction of walkieRangeMeters after which the transmissions are garbled.
Using the defaults, beyond 2000.0/2 = 1000 m the messages will break up, and from 0 - 1000 m, messages 
will be clear.

**walkieChatLineFormat** ("[walkie] %1$s: %2$s"): Messages received from walkie-talkies will
be formatted using this format string. See also chatLineFormat.

**walkieHearLocally** (true): When talking into a walkie-walkie, allow other people nearby to overhear
you. This adds realism since you'll have to whisper or go away from other players to have a private
conversation, but can show doubled messages if the player you're talking to on teh walkie is near enough to hear
you locally as well.  If false, the message will only be sent into the walkie-talkie, not to nearby users.

**globalPrefix** ('/g '): Messages beginning this prefix will be delivered globally, regardless
of local chat restrictions. You can either use a command (examples: '/g ' or '/s '), or a prefix in
normal chat (example: '@'). Set to null to disable.

**globalChatLineFormat** ("[global] %1$s: %2$s"): Messages sent using /g will be formatted using
this format string. See also chatLineFormat.

**smartphoneEnable** (true): Enable smartphones.

**smartphoneItem** (watch): Item to use to represent a smartphone.

**smartphoneChatLineFormat** ("[cell] %1$s: %2$s"): Messages received from smartphones will
be formatted using this format string. See also chatLineFormat.

**smartphoneHearLocally** (true): When talking into a smartphone, allow other people nearby to 
overhear you. See also walkieHearLocally.


CakeMushroom's RealisticChat - realistic local chat!

*a collaboration of cakenggt and mushroomhostage*

Features:

* Local chat 
* Yelling
* Whispering
* Megaphones
* Ear trumpets
* Walkie-talkies

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

To further increase your chat range, speak while holding a **megaphone** (diamond). This will double your
range, and can be used in conjunction with yelling. Players hearing messages from megaphones
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

All distances are configurable to your liking; see below.

## Commands and Permissions
None

## Configuration
**speakingRangeMeters** (50.0): Maximum distance between players to allow any chat message to be received,
under normal conditions. Note that the message may be garbled at longer distances, see below.

**garbleRangeDivisor** (2.0): Fraction of speakingRangeDistance after which the chat is garbled 
(letters randomly dropped with increasing probability further away). For example, 2.0 means beyond 1/2.0 = 
half of the maximum hearing range (default 50/2 = 25 m), chat will be garbled.

**garblePartialChance** (0.10): When a character is about to be dropped due to garbling, use this
probability to determine if the character should instead be dimmed, representing a barely audible piece of
the message. Set to 1.0 to always dim letters instead of dropping, or 0.0 to disable dimming entirely.

**garbleAllDroppedMessage** (~~~): Message replacement to display if you were unlucky enough for your
entire message to be garbled. Set to null to disable replacement.

**chatLineFormat** (player: message): The format of the chat line to be shown to the player. "player"
is replaced with the player's display name, and "message" with the message text.

**yellMax** (4): Maximum exclamation marks allowed at end of message to increase yelling level.

**yell.#.hunger** (1, 2, 4, 20): Hunger (in half-drumsticks) depleted for each yelling level 
(number of trailing exclamation marks). 20 depletes all hunger.

**yell.#.rangeIncrease** (10.0, 50.0, 100.0, 500.0): Range increase in meters, beyond the normal range, for
each yelling level.

**whisperRangeDecrease** (40.0): Distance to decrease the chat range when whispering (parenthesized chat).
For example, 40.0 will decrease the normal 50.0 range to 10.0 meters maximum. The garble range divisor still
applies, so (by default) only those 10.0/2 = 5.0 meters away will hear you clearly.

**megaphoneEnable** (true): Enable or disable megaphones. 

**megaphoneItem** (diamond): The hand-held item to use to represent megaphones.

**megaphoneFactor** (2.0): Multiply chat range by this value when player is holding a megaphone (diamond).

**megaphoneWidthDegrees** (70.0): Width of the megaphone conical region in front of the player, in degrees.
70 is the default Minecraft field-of-view.

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


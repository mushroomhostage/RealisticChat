CakeMushroom's RealisticChat - realistic local chat!

*a collaboration of cakenggt and mushroomhostage*

Features:

* Local chat 
* Yelling
* Whispering
* Megaphones
* Walkie-talkies

## Usage
Talk normally. You'll only be able to clearly hear other players within 25 meters.

Beyond 25 up to 50 meters, chat will break up: letters will randomly drop from your
messages, with increasing probability the greater the distance.

To reach someone further away, you can **yell**. For example: "*help!*" adds 10 m. Multiple exclamation
marks will make you yell louder: "*help!!!!*" adds 500 m. Yelling will also make garbled messages clearer.
However, yelling comes at the cost of decreasing your hunger level!

If you want to talk to people very close by but hide your chat from others further away, you
can decrease your chat range by **whispering**, for example: "*(lets go)*". Only players within
10 m will be able to hear your whispering, and only those within 5 m will be able to hear you clearly.

To further increase your chat range, speak while holding a megaphone (diamond). This will double your
chat range, and can be used in conjunction with yelling. Players hearing messages from megaphones
will recognize the direction it is coming from.

For long-range communication, use a **walkie talkie** (compass). Hold the compass in your *hand*
and speak as normal, and your voice will be transmitted through the device up to 1000 meters
to other players holding a compass anywhere in their *hotbar*. Walkie Talkies are especially
useful for keeping in touch with other players while travelling.

All distances are configurable to your liking; see below.

## Commands and Permissions
None

## Configuration
**hearingRangeMeters** (50.0): Maximum distance between players to allow any chat message to be received.

**garbleRangeDivisor** (2.0): Fraction of hearingRangeDistance after which the chat is garbled 
(letters randomly dropped with increasing probability further away). For example, 2.0 means beyond 1/2.0 = 
half of the maximum hearing range (default 50/2 = 25 m), chat will be garbled.

**yellMax** (4): Maximum exclamation marks allowed at end of message to increase yelling level.

**yell.#.hunger** (1, 2, 4, 20): Hunger (in half-drumsticks) depleted for each yelling level 
(number of trailing exclamation marks). 20 depletes all hunger.

**yell.#.rangeIncrease** (10.0, 50.0, 100.0, 500.0): Range increase in meters, beyond the normal range, for
each yelling level.

**whisperRangeDecrease** (40.0): Distance to decrease the chat range when whispering (parenthesized chat).
For example, 40.0 will decrease the normal 50.0 range to 10.0 meters maximum. The garble range divisor still
applies, so (by default) only those 10.0/2 = 5.0 meters away will hear you clearly.

**megaphoneFactor** (2.0): Multiply chat range by this value when player is holding a megaphone (diamond).

**walkieRange** (1000.0): The range of walkie talkies.



/*
 * Notes
 * Games that need certain settings activated in order to function properly:
 *
 * update mode type -
 * * Street Fighter III Double Impact (would run at choppy framerate otherwise)
 *
 * dynarec_type -
 * * Marvel Vs Capcom 2 (would hang at beginning of second stage if not set to generic_recompiler)
 *
 * aica_interrupt_hack -
 * * Street Fighter Alpha 3 (would hang at startup screen otherwise
 */

struct game_type
{
   char product_number[11];
   int updatemode_type;     /* -1, make no decision, 0 = update mode normal, 1 = update mode fullspeed */
   int dynarec_type;        /* -1, make no decision, 0 = dynamic recompiler, 1 = generic_recompiler */
   int palette_hack;        /* -1, make no decision, 0 = normal, 1 = enable hack */
   int aica_interrupt_hack; /* -1, make no decision, 0 = normal, 1 = enable hack */
   f32 zMax;
};

static struct game_type lut_games[] = 
{
   { "MK-51035  ", -1,  1, -1,  1 },				/* Capcom Vs SNK 2 */
   { "T1213N    ", -1,  1, -1,  1 },				/* Street Fighter III -3rd Strike */
   { "MK-51049  ",  1, -1, -1,  1 },                /* Marvel Vs Capcom 2 */
   { "T1203N    ", -1,  1,  1,  1 },                /* Street Fighter Alpha 3 */
#if defined(__linux__) || defined(__MACH__)
   { "MK0815    ", -1, -1, -1,  3500.0 },           /* Soul Calibur (E) */
   { "T1401M    ", -1, -1, -1,  3500.0 },           /* Soul Calibur (J) */
   { "T1401N    ", -1, -1, -1,  3500.0 },           /* Soul Calibur (U) */
#endif
};

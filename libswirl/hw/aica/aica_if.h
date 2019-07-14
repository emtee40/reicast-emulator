#pragma once
#include "types.h"

extern u32 VREG;
extern VLockedMemory aica_ram;
extern u32 RealTimeClock;
u32 ReadMem_aica_rtc(u32 addr,u32 sz);
void WriteMem_aica_rtc(u32 addr,u32 data,u32 sz);
u32 ReadMem_aica_reg(u32 addr,u32 sz);
void WriteMem_aica_reg(u32 addr,u32 data,u32 sz);

void aica_Init();
void aica_Reset(bool Manual);
void aica_Term();

#define UpdateAica(clc) libAICA_Update(clc)
#define UpdateArm(clc) libARM_Update(clc)

void aica_sb_Init();
void aica_sb_Reset(bool Manual);
void aica_sb_Term();

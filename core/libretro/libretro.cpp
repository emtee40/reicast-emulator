#include "libretro/libretro.h"
#include "libretro/reicast.h"
#include "types.h"
#include <cstdio>
#include <cstdarg>

uint32_t video_width =  640;
uint32_t video_height = 480;


// Callbacks
retro_log_printf_t         log_cb = NULL;
retro_video_refresh_t      video_cb = NULL;
retro_input_poll_t         poll_cb = NULL;
retro_input_state_t        input_cb = NULL;
retro_audio_sample_batch_t audio_batch_cb = NULL;
retro_environment_t        environ_cb = NULL;

extern "C"
{
	void retro_set_video_refresh(retro_video_refresh_t cb)
	{
		video_cb = cb;
	}

	void retro_set_audio_sample(retro_audio_sample_t cb)
	{
		// Nothing to do here
	}

	void retro_set_audio_sample_batch(retro_audio_sample_batch_t cb)
	{
		audio_batch_cb = cb;
	}

	void retro_set_input_poll(retro_input_poll_t cb)
	{
		poll_cb = cb;
	}

	void retro_set_input_state(retro_input_state_t cb)
	{
		input_cb = cb;
	}

	void retro_set_environment(retro_environment_t cb)
	{
		environ_cb = cb;
	}


	// Now comes the interesting stuff
	void retro_init(void)
	{
		// Logging
		struct retro_log_callback log;
		if (environ_cb(RETRO_ENVIRONMENT_GET_LOG_INTERFACE, &log))
			log_cb = log.log;
		else
			log_cb = NULL;

		// Set color mode
		unsigned color_mode = RETRO_PIXEL_FORMAT_XRGB8888;
		environ_cb(RETRO_ENVIRONMENT_SET_PIXEL_FORMAT, &color_mode);

		dc_init(0, NULL);
	}

	void retro_deinit(void)
	{
		//TODO
	}

	void retro_run (void)
	{
		//dc_run();
	}

	void retro_reset (void)
	{
		//TODO
	}


	// Loading/unloading games
	bool retro_load_game(const struct retro_game_info *game)
	{
		//TODO
	}

	bool retro_load_game_special(unsigned game_type, const struct retro_game_info *info, size_t num_info)
	{
		return false; //TODO (?)
	}

	void retro_unload_game(void)
	{
		//TODO
	}


	// Memory/Serialization
	void *retro_get_memory_data(unsigned type)
	{
		return 0; //TODO
	}

	size_t retro_get_memory_size(unsigned type)
	{
		return 0; //TODO
	}

	size_t retro_serialize_size (void)
	{
		return 0; //TODO
	}

	bool retro_serialize(void *data, size_t size)
	{
		return false; //TODO
	}

	bool retro_unserialize(const void * data, size_t size)
	{
		return false; //TODO
	}

	// Cheats
	void retro_cheat_reset(void)
	{
		// Nothing to do here
	}
	void retro_cheat_set(unsigned unused, bool unused1, const char* unused2)
	{
		// Nothing to do here
	}


	// Get info
	const char* retro_get_system_directory(void)
	{
		const char* dir;
		environ_cb(RETRO_ENVIRONMENT_GET_SYSTEM_DIRECTORY, &dir);
		return dir ? dir : ".";
	}

	void retro_get_system_info(struct retro_system_info *info)
	{
		info->library_name = "Reicast";
		info->library_version = "0.1";
		info->valid_extensions = "cdi|gdi|chd";
		info->need_fullpath = true;
		info->block_extract = false;
	}

	void retro_get_system_av_info(struct retro_system_av_info *info)
	{
		info->geometry.base_width   = video_width;
		info->geometry.base_height  = video_height;
		info->geometry.max_width    = video_width;
		info->geometry.max_height   = video_height;
		info->geometry.aspect_ratio = 4.0 / 3.0;
		info->timing.fps = 50.0; //FIXME: This might differ for non-PAL games
		info->timing.sample_rate = 44100.0;
	}

	unsigned retro_get_region (void)
	{
	   return RETRO_REGION_PAL; //TODO
	}


	// Controller
	void retro_set_controller_port_device(unsigned in_port, unsigned device)
	{
		//TODO
	}


	// API version (to detect version mismatch)
	unsigned retro_api_version(void)
	{
		return RETRO_API_VERSION;
	}
}


//Reicast stuff

u16 kcode[4] = {0xFFFF, 0xFFFF, 0xFFFF, 0xFFFF};
u8 rt[4] = {0, 0, 0, 0};
u8 lt[4] = {0, 0, 0, 0};
u32 vks[4];
s8 joyx[4], joyy[4];

void os_DoEvents()
{
	//TODO
}

void os_CreateWindow()
{
	// Nothing to do here
}

void UpdateInputState(u32 port)
{
	//TODO
}

void* libPvr_GetRenderTarget()
{
	return NULL;
}

void* libPvr_GetRenderSurface()
{
	return NULL;
}

void gl_init(void* disp, void* win)
{
	//TODO
}

void os_SetWindowText(const char * text)
{
	// Nothing to do here
}

void gl_swap()
{
	//TODO
}

int get_mic_data(u8* buffer)
{
	return 0;
}

int push_vmu_screen(u8* buffer)
{
	return 0;
}

int msgboxf(const char* text, unsigned int type, ...)
{
	if (log_cb)
	{
		va_list args;

		char temp[2048];

		va_start(args, type);
		vsprintf(temp, text, args);
		va_end(args);

		log_cb(RETRO_LOG_INFO, temp);
	}
	return 0;
}

void os_DebugBreak()
{
	// Nothing to do here
}
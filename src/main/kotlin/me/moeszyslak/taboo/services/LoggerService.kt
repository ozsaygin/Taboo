package me.moeszyslak.taboo.services

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.moeszyslak.taboo.data.Configuration
import me.moeszyslak.taboo.data.FileWrapper
import me.moeszyslak.taboo.extensions.descriptor

@Service
class LoggerService(private val configuration: Configuration, private val discord: Discord) {

    private suspend fun withLog(guild: Guild, f: () -> String) =
            getLogConfig(guild.id.longValue).apply {
                log(guild, getLogConfig(guild.id.longValue), f())
            }


    suspend fun logDeleted(guild: Guild, member: Member, channel: MessageChannel, fileWrapper: FileWrapper) = withLog(guild) {
        "Deleted ${fileWrapper.fileMetadata.name} " +
                "with a MIME of ${fileWrapper.fileMetadata.type} " +
                "in ${channel.mention} " +
                "from ${member.descriptor()}"
    }

    suspend fun logUploaded(guild: Guild, member: Member, channel: MessageChannel, fileWrapper: FileWrapper) = withLog(guild) {
        "Deleted and uploaded ${fileWrapper.fileMetadata.name} " +
                "with a MIME of ${fileWrapper.fileMetadata.type} " +
                "in ${channel.mention} " +
                "from ${member.descriptor()}"
    }


    private fun getLogConfig(guildId: Long) = configuration.guildConfigurations[guildId]!!.logChannel
    private suspend fun log(guild: Guild, logChannelId: Long, message: String) = logChannelId.idToTextChannel(guild)
            ?.createMessage(message)

    private suspend fun Long.idToTextChannel(guild: Guild) = discord.api.getChannelOf<MessageChannel>(Snowflake(this))
}
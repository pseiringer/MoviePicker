using System.Threading.Channels;

namespace MoviePickerBackend.Logic.BackgroundServices
{
    public class RoomRemovalChannel
    {
        private readonly Channel<string> ch;

        public RoomRemovalChannel()
        {
            this.ch = Channel.CreateUnbounded<string>(new UnboundedChannelOptions { 
                SingleReader = true,
                SingleWriter = false
            });
        }

        public async Task<bool> AddRoomForRemoval(string roomCode, CancellationToken ct = default)
        {
            await ch.Writer.WriteAsync(roomCode, ct);
            return !ct.IsCancellationRequested;
        }
        public IAsyncEnumerable<string> GetRooms(CancellationToken ct = default)
        {
            return ch.Reader.ReadAllAsync(ct);
        }
        public bool TryComplete(Exception ex)
        {
            return ch.Writer.TryComplete(ex);
        }
    }
}

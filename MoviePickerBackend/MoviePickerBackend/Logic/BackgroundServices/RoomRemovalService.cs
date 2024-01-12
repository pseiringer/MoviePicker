
using Microsoft.EntityFrameworkCore;
using MoviePickerBackend.Logic.Interfaces;
using System.Threading.Channels;

namespace MoviePickerBackend.Logic.BackgroundServices
{
    public class RoomRemovalService : BackgroundService
    {
        private const int DELAY_TIME = 86_400_000; // 24 hours -> 86_400_000 milliseconds
        private readonly RoomRemovalChannel ch;
        private readonly IServiceScopeFactory scopeFactory;

        public RoomRemovalService(RoomRemovalChannel ch, IServiceScopeFactory scopeFactory)
        {
            this.ch = ch;
            this.scopeFactory = scopeFactory;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            await foreach(var code in ch.GetRooms(stoppingToken))
            {
                var currentCode = code;
#pragma warning disable CS4014 // Because this call is not awaited, execution of the current method continues before the call is completed
                new TaskFactory().StartNew(async () =>
                {
                    await Task.Delay(DELAY_TIME, stoppingToken);
                    if (stoppingToken.IsCancellationRequested)
                        return;
                    using (var scope = scopeFactory.CreateScope())
                    {
                        var logic = scope.ServiceProvider.GetRequiredService<IRoomLogic>();
                        await logic.RemoveRoom(currentCode);

                    }
                });
#pragma warning restore CS4014 // Because this call is not awaited, execution of the current method continues before the call is completed
            }
        }
    }
}

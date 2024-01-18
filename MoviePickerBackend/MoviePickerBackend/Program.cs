using Microsoft.AspNetCore.Builder;
using MoviePickerBackend.Logic.BackgroundServices;
using MoviePickerBackend.Logic.Implementations;
using MoviePickerBackend.Logic.Interfaces;
using MoviePickerBackend.Models;

namespace MoviePickerBackend
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.
            builder.Services.AddDbContext<MoviePickerContext>();

            builder.Services.AddScoped<IRoomLogic, RoomLogic>();
            builder.Services.AddSingleton<RoomRemovalChannel>();
            builder.Services.AddHostedService<RoomRemovalService>();

            builder.Services.AddAutoMapper(typeof(Program));

            builder.Services.AddOpenApiDocument(options =>
            {
                options.PostProcess = doc => doc.Info.Title = "Movie Picker API";
            });

            builder.Services.AddControllers();



            var app = builder.Build();

            // Configure the HTTP request pipeline.
            //if (app.Environment.IsDevelopment())
            //{
                app.UseOpenApi();
                app.UseSwaggerUi(setting => setting.Path = "/swagger");
                app.UseReDoc(setting => setting.Path = "/redoc");
            //}

            //app.UseHttpsRedirection();

            app.UseAuthorization();


            app.MapControllers();

            using (var serviceScope = app.Services.GetService<IServiceScopeFactory>().CreateScope())
            {
                var context = serviceScope.ServiceProvider.GetRequiredService<MoviePickerContext>();
                context.Database.EnsureDeleted();
                context.Database.EnsureCreated();
            }


            app.Run();
        }
    }
}

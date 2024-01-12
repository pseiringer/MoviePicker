using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Hosting;
using System.Reflection.Metadata;

namespace MoviePickerBackend.Models
{
    public class MoviePickerContext: DbContext
    {
        public DbSet<Room> Rooms { get; set; }
        public DbSet<VoteSum> VoteSums { get; set; }

        public string DbPath { get; }

        public MoviePickerContext()
        {
            //var folder = Environment.SpecialFolder.LocalApplicationData;
            //var path = Environment.GetFolderPath(folder);
            //DbPath = System.IO.Path.Join(path, "moviePicker.db");
            DbPath = "C:\\Temp\\moviePicker.db";
        }

        // The following configures EF to create a Sqlite database file in the
        // special "local" folder for your platform.
        protected override void OnConfiguring(DbContextOptionsBuilder options)
        {
            options.UseSqlite($"Data Source={DbPath}");
        }
    }
}

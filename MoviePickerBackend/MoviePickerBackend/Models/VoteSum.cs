using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MoviePickerBackend.Models
{
    [PrimaryKey(nameof(RoomCode), nameof(MovieId))]
    public class VoteSum
    {
        [ForeignKey(nameof(Room))]
        public string RoomCode { get; set; }
        public int MovieId { get; set; }
        public int NumVotes { get; set; }
    }
}

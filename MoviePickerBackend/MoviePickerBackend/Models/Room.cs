using System.ComponentModel.DataAnnotations;

namespace MoviePickerBackend.Models
{
    public class Room
    {
        [Key]
        public string RoomCode { get; set; }
        public bool IsClosed { get; set; }
        public List<VoteSum> Votes { get; set; }
    }
}

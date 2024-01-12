namespace MoviePickerBackend.DTOs
{
    public class RoomDTO
    {
        public required string RoomCode { get; set; }
        public required bool IsClosed { get; set; }
        public required IEnumerable<VoteSumDTO> Votes { get; set; }
    }
}

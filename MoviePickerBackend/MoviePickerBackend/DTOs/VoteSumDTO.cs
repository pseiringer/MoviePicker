namespace MoviePickerBackend.DTOs
{
    public class VoteSumDTO
    {
        public required string RoomCode { get; set; }
        public required int MovieId { get; set; }
        public required int NumVotes { get; set; }
    }
}

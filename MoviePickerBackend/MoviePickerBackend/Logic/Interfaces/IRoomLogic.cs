using MoviePickerBackend.DTOs;
using MoviePickerBackend.Logic.Results;

namespace MoviePickerBackend.Logic.Interfaces
{
    public interface IRoomLogic
    {
        public Task<LogicResult<RoomDTO>> CreateRoom();
        public Task<LogicResult<RoomDTO>> GetRoom(string roomCode);
        public Task<LogicResult> Vote(string roomCode, VoteDTO vote);
        public Task<LogicResult> CloseRoom(string roomCode);
        public Task<LogicResult> RemoveRoom(string roomCode);

    }
}

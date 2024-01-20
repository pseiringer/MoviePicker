using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using MoviePickerBackend.DTOs;
using MoviePickerBackend.Logic.Interfaces;
using MoviePickerBackend.Logic.Results;
using System;

namespace MoviePickerBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class RoomsController : Controller
    {
        private readonly IRoomLogic logic;

        public RoomsController(IRoomLogic logic)
        {
            this.logic = logic;
        }

        [HttpGet("{roomCode}")]
        public async Task<ActionResult<RoomDTO>> GetRoom([FromRoute] string roomCode)
        {
            var result = await logic.GetRoom(roomCode);
            if (!result.Success)
            {
                switch (result.Error)
                {
                    case ErrorResult.NotFound:
                        return NotFound();
                    default:
                        throw new InvalidOperationException($"Unexpected result: {result}");
                }
            }
            return Ok(result.Value);
        }
        [HttpPost]
        public async Task<ActionResult<RoomDTO>> CreateRoom()
        {
            var result = await logic.CreateRoom();
            if (!result.Success)
            {
                switch (result.Error)
                {
                    default:
                        throw new InvalidOperationException($"Unexpected result: {result}");
                }
            }
            return Ok(result.Value);
        }
        [HttpPost("{roomCode}/Votes")]
        public async Task<ActionResult> Vote([FromRoute] string roomCode, [FromBody] VoteDTO vote)
        {
            var result = await logic.Vote(roomCode, vote);
            if (!result.Success)
            {
                switch (result.Error)
                {
                    case ErrorResult.NotFound:
                        return NotFound();
                    case ErrorResult.Closed:
                        return BadRequest(new ProblemDetails() { Title = "Room Closed", Detail = "This room has already been closed" });
                    default:
                        throw new InvalidOperationException($"Unexpected result: {result}");
                }
            }
            return NoContent();
        }
        [HttpPost("{roomCode}/VoteList")]
        public async Task<ActionResult> VoteList([FromRoute] string roomCode, [FromBody] VoteListDTO votes)
        {
            var result = await logic.VoteList(roomCode, votes);
            if (!result.Success)
            {
                switch (result.Error)
                {
                    case ErrorResult.NotFound:
                        return NotFound();
                    case ErrorResult.Closed:
                        return BadRequest(new ProblemDetails() { Title = "Room Closed", Detail = "This room has already been closed" });
                    default:
                        throw new InvalidOperationException($"Unexpected result: {result}");
                }
            }
            return NoContent();
        }
        [HttpPut("{roomCode}")]
        public async Task<ActionResult> CloseRoom([FromRoute] string roomCode)
        {
            var result = await logic.CloseRoom(roomCode);
            if (!result.Success)
            {
                switch (result.Error)
                {
                    case ErrorResult.NotFound:
                        return NotFound();
                    default:
                        throw new InvalidOperationException($"Unexpected result: {result}");
                }
            }
            return NoContent();
        }
        [HttpDelete("{roomCode}")]
        public async Task<ActionResult> RemoveRoom([FromRoute] string roomCode)
        {
            var result = await logic.RemoveRoom(roomCode);
            if (!result.Success)
            {
                switch (result.Error)
                {
                    case ErrorResult.NotFound:
                        return NotFound();
                    default:
                        throw new InvalidOperationException($"Unexpected result: {result}");
                }
            }
            return NoContent();
        }

    }
}

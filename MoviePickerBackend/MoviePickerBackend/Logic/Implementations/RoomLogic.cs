using AutoMapper;
using Microsoft.EntityFrameworkCore;
using MoviePickerBackend.DTOs;
using MoviePickerBackend.Logic.BackgroundServices;
using MoviePickerBackend.Logic.Interfaces;
using MoviePickerBackend.Logic.Results;
using MoviePickerBackend.Models;
using System.Collections.Immutable;
using System.Security.Cryptography;
using System.Text;

namespace MoviePickerBackend.Logic.Implementations
{
    public class RoomLogic : IRoomLogic
    {
        //private List<Room> rooms = new List<Room>();

        private IMapper mapper;
        private readonly RoomRemovalChannel ch;
        private readonly MoviePickerContext db;
        private Random rng = new Random();

        public RoomLogic(IMapper mapper, RoomRemovalChannel ch, MoviePickerContext db) {
            this.mapper = mapper;
            this.ch = ch;
            this.db = db;
        }

        public Task<LogicResult> CloseRoom(string roomCode)
        {
            var room = db.Rooms.FirstOrDefault(x => x.RoomCode == roomCode);
            if (room == null)
            {
                return Task.FromResult(new LogicResult(ErrorResult.NotFound));
            }
            else
            {
                room.IsClosed = true;
                db.Update(room);
                db.SaveChanges();
                return Task.FromResult(new LogicResult());
            }
        }

        public async Task<LogicResult<RoomDTO>> CreateRoom()
        {
            var room = new Room
            {
                RoomCode = GetNewRoomCode(),
                IsClosed = false,
                Votes = new List<VoteSum>()
            };
            db.Rooms.Add(room);
            db.SaveChanges();
            await ch.AddRoomForRemoval(room.RoomCode);
            return new LogicResult<RoomDTO>(mapper.Map<RoomDTO>(room));
        }

        public Task<LogicResult<RoomDTO>> GetRoom(string roomCode)
        {
            var room = db.Rooms
                .Include(x => x.Votes)
                .FirstOrDefault(x => x.RoomCode == roomCode);
            return Task.FromResult(
                (room != null) ? 
                    new LogicResult<RoomDTO>(mapper.Map<RoomDTO>(room)) : 
                    new LogicResult<RoomDTO>(ErrorResult.NotFound)
            );
        }

        public Task<LogicResult> RemoveRoom(string roomCode)
        {
            var rooms = db.Rooms.Where(x => x.RoomCode == roomCode);
            if (rooms.Count() == 0)
                return Task.FromResult(new LogicResult());

            db.Rooms.RemoveRange(rooms);
            db.SaveChanges();
            return Task.FromResult(new LogicResult());
        }

        public Task<LogicResult> Vote(string roomCode, VoteDTO vote)
        {
            var room = db.Rooms.FirstOrDefault(x => x.RoomCode == roomCode);
            if (room == null)
            {
                return Task.FromResult(new LogicResult(ErrorResult.NotFound));
            }
            if (room.IsClosed)
            {
                return Task.FromResult(new LogicResult(ErrorResult.Closed));
            }
            else
            {
                var voteSum = db.VoteSums.FirstOrDefault(x => x.MovieId == vote.MovieId && x.RoomCode == roomCode);
                if (voteSum == null)
                {
                    voteSum = new VoteSum
                    {
                        RoomCode = roomCode,
                        MovieId = vote.MovieId,
                        NumVotes = 1,
                    };
                    db.VoteSums.Add(voteSum);
                }
                else
                {
                    voteSum.NumVotes++;
                    db.VoteSums.Update(voteSum);
                }
                db.SaveChanges();

                return Task.FromResult(new LogicResult());
            }
        }

        private string GetNewRoomCode()
        {
            string valid = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            int numSymbols = (int) Math.Ceiling(Math.Log(db.Rooms.Count() * valid.Length) / (Math.Log(valid.Length)));
            if (numSymbols < 4)
            {
                numSymbols = 4;
            }

            string? code = null;
            while(code == null)
            {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < numSymbols; i++)
                {
                    sb.Append(valid[rng.Next(0, valid.Length)]);
                }
                string newCode = sb.ToString();
                if (!db.Rooms.Any(x => x.RoomCode == newCode)) {
                    code = newCode;
                }
            }
            return code;
        }
    }
}

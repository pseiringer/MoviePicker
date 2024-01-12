using AutoMapper;
using MoviePickerBackend.DTOs;
using MoviePickerBackend.Models;

namespace MoviePickerBackend.Profiles
{
    public class MapperProfile: Profile
    {
        public MapperProfile()
        {
            CreateMap<RoomDTO, Room>()
                .ReverseMap();
            CreateMap<VoteSumDTO, VoteSum>()
                .ReverseMap();
        }
    }
}

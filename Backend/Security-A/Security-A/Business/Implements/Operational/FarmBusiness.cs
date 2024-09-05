using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto;
using Entity.Dto.Operational;
using Entity.Model.Operational;

namespace Business.Implements.Operational
{
    public class FarmBusiness : IFarmBusiness
    {
        private readonly IFarmData data;

        public FarmBusiness(IFarmData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<FarmDto>> GetAll()
        {
            IEnumerable<Farm> farms = await data.GetAll();
            var FarmDtos = farms.Select(Farm => new FarmDto
            {
                Id = Farm.Id,
                Name = Farm.Name,
                UserId = (int)Farm.UserId,
                CityId = (int)Farm.CityId,
                State = Farm.State,
            });

            return FarmDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<FarmDto> GetById(int id)
        {
            Farm farm = await data.GetById(id);
            FarmDto farmDto = new FarmDto();
            farmDto.Id = farm.Id;
            farmDto.CityId = (int)farm.CityId;
            farmDto.UserId = (int)farm.UserId;
            farmDto.Name = farm.Name;
            farmDto.State = farm.State;
            return farmDto;
        }

        public Farm mapearDatos(Farm farm, FarmDto entity)
        {
            farm.Id = entity.Id;
            farm.CityId = entity.CityId;
            farm.UserId = entity.UserId;
            farm.Name = entity.Name;
            farm.State = entity.State;
            return farm;    
        }

        public async Task<Farm> Save(FarmDto entity)
        {
            Farm farm = new Farm();
            farm = mapearDatos(farm, entity);
            farm.Created_at = DateTime.Now;
            farm.Updated_at = null;
            farm.Deleted_at = null;

            return await data.Save(farm);
        }

        public async Task Update(FarmDto entity)
        {
            Farm farm = await data.GetById(entity.Id);
            if (farm == null)
            {
                throw new Exception("Registro no encontrado");
            }
            farm = mapearDatos(farm, entity);
            farm.Updated_at = DateTime.Now;

            await data.Update(farm);
        }
    }
}

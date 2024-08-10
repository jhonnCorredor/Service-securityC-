using Business.Interfaces.Parameter;
using Data.Interfaces.Parameter;
using Entity.Dto;
using Entity.Dto.Parameter;
using Entity.Model.Parameter;

namespace Business.Implements.Parameter
{
    public class ContinentBusiness :IContinentBusiness
    {
        private readonly IContinentData data;

        public ContinentBusiness(IContinentData data)
        {
            this.data = data; 
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<ContinentDto>> GetAll()
        {
            IEnumerable<Continent> continents = await data.GetAll();
            var ContinentDTOs = continents.Select(Continent => new ContinentDto
            {
                Id = Continent.Id,
                Name = Continent.Name,
                Description = Continent.Description,
                Code = Continent.Code,
                State = Continent.State
            });

            return ContinentDTOs;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<ContinentDto> GetById(int id)
        {
            Continent continent = await data.GetById(id);
            ContinentDto continentDto = new ContinentDto();
            continentDto.Id = continent.Id;
            continentDto.Name = continent.Name;
            continentDto.Description = continent.Description;
            continentDto.Code = continent.Code;
            continentDto.State = continent.State;
            return continentDto;
        }

        public  Continent mapearDatos(Continent continent, ContinentDto entity)
        {
            continent.Id = entity.Id;
            continent.Name = entity.Name;
            continent.Description = entity.Description;
            continent.Code = entity.Code;
            continent.State = entity.State;
            return continent;
        }

        public async Task<Continent> Save(ContinentDto entity)
        {
            Continent continent = new Continent();
            continent = mapearDatos(continent, entity);
            continent.Created_at = DateTime.Now;
            continent.Updated_at = null;
            continent.Deleted_at = null;

            return await data.Save(continent);
        }

        public async Task Update(ContinentDto entity)
        {
            Continent continent = await data.GetById(entity.Id);
            if (continent == null)
            {
                throw new Exception("Registro no encontrado");
            }
            continent = mapearDatos(continent, entity);
            continent.Updated_at = DateTime.Now;

            await data.Update(continent);
        }
    }
}

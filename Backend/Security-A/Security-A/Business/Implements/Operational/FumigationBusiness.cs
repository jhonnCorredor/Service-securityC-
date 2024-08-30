using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto.Operational;
using Entity.Dto;
using Entity.Model.Operational;

namespace Business.Implements.Operational
{
    public class FumigationBusiness : IFumigationBusiness
    {
        private readonly IFumigationData data;

        public FumigationBusiness(IFumigationData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<FumigationDto>> GetAll()
        {
            IEnumerable<Fumigation> Fumigations = await data.GetAll();
            var FumigationDtos = Fumigations.Select(Fumigation => new FumigationDto
            {
                Id = Fumigation.Id,
                DateFumigation = Fumigation.DateFumigation,
                QuantityMix = Fumigation.QuantityMix,
                State = Fumigation.State
            });

            return FumigationDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<FumigationDto> GetById(int id)
        {
            Fumigation Fumigation = await data.GetById(id);
            FumigationDto dto = new FumigationDto();
            dto.Id = Fumigation.Id;
            dto.DateFumigation = Fumigation.DateFumigation;
            dto.QuantityMix = Fumigation.QuantityMix;
            dto.State = Fumigation.State;
            return dto;
        }

        public Fumigation mapearDatos(Fumigation fumigation, FumigationDto entity)
        {
            fumigation.Id = entity.Id;
            fumigation.DateFumigation = entity.DateFumigation;
            fumigation.QuantityMix = entity.QuantityMix;
            fumigation.State = entity.State;
            return fumigation;
        }

        public async Task<Fumigation> Save(FumigationDto entity)
        {
            Fumigation Fumigation = new Fumigation();
            Fumigation = mapearDatos(Fumigation, entity);
            Fumigation.Created_at = DateTime.Now;
            Fumigation.Updated_at = null;
            Fumigation.Deleted_at = null;

            return await data.Save(Fumigation);
        }

        public async Task Update(FumigationDto entity)
        {
            Fumigation Fumigation = await data.GetById(entity.Id);
            if (Fumigation == null)
            {
                throw new Exception("Registro no encontrado");
            }
            Fumigation = mapearDatos(Fumigation, entity);
            Fumigation.Updated_at = DateTime.Now;

            await data.Update(Fumigation);
        }
    }
}

using Business.Interfaces.Parameter;
using Data.Interfaces.Operational;
using Entity.Dto;
using Entity.Dto.Parameter;
using Entity.Model.Parameter;

namespace Business.Implements.Operational
{
    public class FumigationSuppliesBusiness : IFumigationSuppliesBusiness
    {
        private readonly IFumigationSuppliesData data;

        public FumigationSuppliesBusiness(IFumigationSuppliesData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<FumigationSuppliesDto>> GetAll()
        {
            IEnumerable<FumigationSupplies> FumigationSuppliess = await data.GetAll();
            var FumigationSuppliesDtos = FumigationSuppliess.Select(FumigationSupplies => new FumigationSuppliesDto
            {
                Id = FumigationSupplies.Id,
                Dose = FumigationSupplies.Dose,
                FumigationId = FumigationSupplies.FumigationId,
                SuppliesId = FumigationSupplies.SuppliesId,
                State = FumigationSupplies.State
            });

            return FumigationSuppliesDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<FumigationSuppliesDto> GetById(int id)
        {
            FumigationSupplies FumigationSupplies = await data.GetById(id);
            FumigationSuppliesDto dto = new FumigationSuppliesDto();
            dto.Id = FumigationSupplies.Id;
            dto.FumigationId = FumigationSupplies.FumigationId;
            dto.SuppliesId = FumigationSupplies.SuppliesId;
            dto.Dose = FumigationSupplies.Dose;
            dto.State = FumigationSupplies.State;
            return dto;
        }

        public FumigationSupplies mapearDatos(FumigationSupplies fumigationSupplies, FumigationSuppliesDto entity)
        {
            fumigationSupplies.Id = entity.Id;
            fumigationSupplies.FumigationId = entity.FumigationId;
            fumigationSupplies.SuppliesId = entity.SuppliesId;
            fumigationSupplies.Dose = entity.Dose;
            fumigationSupplies.State = entity.State;
            return fumigationSupplies;
        }

        public async Task<FumigationSupplies> Save(FumigationSuppliesDto entity)
        {
            FumigationSupplies FumigationSupplies = new FumigationSupplies();
            FumigationSupplies = mapearDatos(FumigationSupplies, entity);
            FumigationSupplies.Created_at = DateTime.Now;
            FumigationSupplies.Updated_at = null;
            FumigationSupplies.Deleted_at = null;

            return await data.Save(FumigationSupplies);
        }

        public async Task Update(FumigationSuppliesDto entity)
        {
            FumigationSupplies FumigationSupplies = await data.GetById(entity.Id);
            if (FumigationSupplies == null)
            {
                throw new Exception("Registro no encontrado");
            }
            FumigationSupplies = mapearDatos(FumigationSupplies, entity);
            FumigationSupplies.Updated_at = DateTime.Now;

            await data.Update(FumigationSupplies);
        }
    }
}

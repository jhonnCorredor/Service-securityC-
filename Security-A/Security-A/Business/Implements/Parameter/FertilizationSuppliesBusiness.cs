using Data.Interfaces.Operational;
using Entity.Dto;
using Entity.Dto.Parameter;
using Entity.Model.Parameter;
using Business.Interfaces.Parameter;

namespace Business.Implements.Operational
{
    public class FertilizationSuppliesBusiness : IFertilizationSuppliesBusiness
    {
        private readonly IFertilizationSuppliesData data;

        public FertilizationSuppliesBusiness(IFertilizationSuppliesData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<FertilizationSuppliesDto>> GetAll()
        {
            IEnumerable<FertilizationSupplies> FertilizationSuppliess = await data.GetAll();
            var FertilizationSuppliesDtos = FertilizationSuppliess.Select(FertilizationSupplies => new FertilizationSuppliesDto
            {
                Id = FertilizationSupplies.Id,
                Dose = FertilizationSupplies.Dose,
                FertilizationId = FertilizationSupplies.FertilizationId,
                SuppliesId = FertilizationSupplies.SuppliesId,
                State = FertilizationSupplies.State
            });

            return FertilizationSuppliesDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<FertilizationSuppliesDto> GetById(int id)
        {
            FertilizationSupplies FertilizationSupplies = await data.GetById(id);
            FertilizationSuppliesDto dto = new FertilizationSuppliesDto();
            dto.Id = FertilizationSupplies.Id;
            dto.SuppliesId = FertilizationSupplies.SuppliesId;
            dto.FertilizationId = FertilizationSupplies.FertilizationId;
            dto.Dose = FertilizationSupplies.Dose;
            dto.State = FertilizationSupplies.State;
            return dto;
        }

        public FertilizationSupplies mapearDatos(FertilizationSupplies fertilizationSupplies, FertilizationSuppliesDto entity)
        {
            fertilizationSupplies.Id = entity.Id;
            fertilizationSupplies.SuppliesId = entity.SuppliesId;
            fertilizationSupplies.FertilizationId = entity.FertilizationId;
            fertilizationSupplies.Dose = entity.Dose;
            fertilizationSupplies.State = entity.State;
            return fertilizationSupplies;
        }

        public async Task<FertilizationSupplies> Save(FertilizationSuppliesDto entity)
        {
            FertilizationSupplies FertilizationSupplies = new FertilizationSupplies();
            FertilizationSupplies = mapearDatos(FertilizationSupplies, entity);
            FertilizationSupplies.Created_at = DateTime.Now;
            FertilizationSupplies.Updated_at = null;
            FertilizationSupplies.Deleted_at = null;

            return await data.Save(FertilizationSupplies);
        }

        public async Task Update(FertilizationSuppliesDto entity)
        {
            FertilizationSupplies FertilizationSupplies = await data.GetById(entity.Id);
            if (FertilizationSupplies == null)
            {
                throw new Exception("Registro no encontrado");
            }
            FertilizationSupplies = mapearDatos(FertilizationSupplies, entity);
            FertilizationSupplies.Updated_at = DateTime.Now;

            await data.Update(FertilizationSupplies);
        }
    }
}

using Entity.Dto;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Entity.Dto.Parameter;
using Entity.Model.Parameter;

namespace Business.Interfaces.Parameter
{
    public interface IFumigationSuppliesBusiness
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<FumigationSuppliesDto> GetById(int id);
        Task<FumigationSupplies> Save(FumigationSuppliesDto entity);
        Task Update(FumigationSuppliesDto entity);
        FumigationSupplies mapearDatos(FumigationSupplies fumigationSupplies, FumigationSuppliesDto entity);
        Task<IEnumerable<FumigationSuppliesDto>> GetAll();
    }
}
